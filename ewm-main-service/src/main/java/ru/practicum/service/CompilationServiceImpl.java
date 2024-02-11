package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatisticsClient;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.ewm.StatsDtoResponse;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.RequestStatus;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;
    private final StatisticsClient statisticsClient;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Optional<Boolean> pinned, Integer from, Integer size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Compilation> compilationPage;
        if (pinned.isEmpty()) {
            compilationPage = compilationRepository.findAll(pageRequest);
        } else {
            compilationPage = compilationRepository.findAllByPinned(pinned.get(), pageRequest);
        }
        List<Compilation> compilations = compilationPage.getContent();
        Set<Event> events = new HashSet<>();
        for (Compilation c : compilations) {
            events.addAll(c.getEvents());
        }
        List<CompilationDto> compilationDtos = compilationMapper.compilationToCompilationDto(compilations);
        Map<Long, Long> confirmedRequests = getConfirmedRequestsId(new ArrayList<>(events));
        return compilationDtos.stream()
                .map(c -> setConfirmedReviewsAndViewsForEventShortDto(new ArrayList<>(c.getEvents()), confirmedRequests, c))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "compilations", key = "#compId")
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Compilation with id=%d was not found", compId)));
        CompilationDto compilationDto = compilationMapper.compilationToCompilationDto(compilation);
        List<Event> events = new ArrayList<>(compilation.getEvents());
        Map<Event, Long> confirmedRequests = getConfirmedRequests(new ArrayList<>(events));
        return setConfirmedReviewsAndViews(events, confirmedRequests, compilationDto);
    }

    @Override
    @Transactional
    public CompilationDto createCompilationAdmin(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.newCompilationDtoToCompilation(newCompilationDto);
        Set<Event> events = new HashSet<>();
        if (newCompilationDto.getEvents() != null) {
            events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        }
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        CompilationDto compilationDto = compilationMapper.compilationToCompilationDto(compilation);
        Map<Event, Long> confirmedRequests = getConfirmedRequests(new ArrayList<>(events));
        return setConfirmedReviewsAndViews(new ArrayList<>(events), confirmedRequests, compilationDto);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "compilations", key = "#compId")
    public void deleteCompilationAdmin(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new EntityNotFoundException(String.format("Compilation with id=%d was not found", compId));
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilationAdmin(UpdateCompilationRequest updateCompilationRequest, Long compId) {
        Compilation compilationToUpdate = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Compilation with id=%d was not found", compId)));
        Set<Event> events = new HashSet<>();
        if (updateCompilationRequest.getEvents() != null) {
            events = eventRepository.findAllByIdIn(updateCompilationRequest.getEvents());
        }
        Compilation updatedCompilation = compilationMapper.updateFromDto(compilationToUpdate, updateCompilationRequest, events);
        compilationRepository.save(updatedCompilation);
        CompilationDto compilationDto = compilationMapper.compilationToCompilationDto(updatedCompilation);
        Map<Event, Long> confirmedRequests = getConfirmedRequests(new ArrayList<>(events));
        return setConfirmedReviewsAndViews(new ArrayList<>(events), confirmedRequests, compilationDto);
    }

    private CompilationDto setConfirmedReviewsAndViews(List<Event> events, Map<Event, Long> confirmedRequests,
                                                       CompilationDto compilationDto) {
        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .collect(Collectors.toList());
        List<StatsDtoResponse> viewStats = statisticsClient.getStats(LocalDateTime.now().minusYears(100),
                LocalDateTime.now(), uris, true);
        Map<Long, Long> eventViews = viewStats.stream()
                .filter(v -> !("/events".equals(v.getUri())))
                .collect(Collectors.toMap(
                        v -> Long.parseLong(String.valueOf(v.getUri().charAt(v.getUri().length() - 1))),
                        v -> v.getHits()
                ));
        List<EventShortDto> eventsWithRequestsAndViews = new ArrayList<>();
        if (!events.isEmpty()) {
            for (Event event : events) {
                Long views = eventViews.get(event.getId());
                Long confirmedRequestsCount = confirmedRequests.get(event);
                EventShortDto eventDto = eventMapper.eventToEventShortDto(event, confirmedRequestsCount, views);
                eventsWithRequestsAndViews.add(eventDto);
            }
        }
        compilationDto.setEvents(new HashSet<>(eventsWithRequestsAndViews));
        return compilationDto;
    }

    private Map<Event, Long> getConfirmedRequests(List<Event> events) {
        return requestRepository.findAllByEventInAndStatus(events, RequestStatus.CONFIRMED).stream()
                .collect(Collectors.groupingBy(ParticipationRequest::getEvent, Collectors.counting()));
    }

    private Map<Long, Long> getConfirmedRequestsId(List<Event> events) {
        return requestRepository.findAllByEventInAndStatus(events, RequestStatus.CONFIRMED).stream()
                .collect(Collectors.groupingBy(p -> p.getEvent().getId(), Collectors.counting()));
    }

    private CompilationDto setConfirmedReviewsAndViewsForEventShortDto(List<EventShortDto> events, Map<Long, Long> confirmedRequests,
                                                       CompilationDto compilationDto) {
        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .collect(Collectors.toList());
        List<StatsDtoResponse> viewStats = statisticsClient.getStats(LocalDateTime.now().minusYears(100),
                LocalDateTime.now(), uris, true);
        Map<Long, Long> eventViews = viewStats.stream()
                .filter(v -> !("/events".equals(v.getUri())))
                .collect(Collectors.toMap(
                        v -> Long.parseLong(String.valueOf(v.getUri().charAt(v.getUri().length() - 1))),
                        v -> v.getHits()
                ));
        List<EventShortDto> eventsWithRequestsAndViews = new ArrayList<>();
        if (!events.isEmpty()) {
            for (EventShortDto event : events) {
                Long views = eventViews.get(event.getId());
                Long confirmedRequestsCount = confirmedRequests.get(event.getId());
                event.setViews(Objects.requireNonNullElse(views, 0L));
                event.setConfirmedRequests(Objects.requireNonNullElse(confirmedRequestsCount, 0L));
                eventsWithRequestsAndViews.add(event);
            }
        }
        compilationDto.setEvents(new HashSet<>(eventsWithRequestsAndViews));
        return compilationDto;
    }
}