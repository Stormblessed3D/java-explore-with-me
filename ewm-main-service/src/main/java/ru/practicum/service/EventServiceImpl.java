package ru.practicum.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatisticsClient;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.ewm.StatsDtoResponse;
import ru.practicum.exception.EventStatusException;
import ru.practicum.exception.EventTimeException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.Qclasses.QEvent;
import ru.practicum.model.RequestStatus;
import ru.practicum.model.SortParameter;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.util.EventsAdminRequest;
import ru.practicum.util.RequestShortCount;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final StatisticsClient statisticsClient;

    @Override
    @Transactional
    public EventFullDto createEvent(NewEventDto eventDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%d was not found", userId)));
        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Category with id=%d was not found",
                        eventDto.getCategory())));
        Event event = eventMapper.newEventDtoToEvent(eventDto, user, category);
        return eventMapper.eventToEventFullDto(eventRepository.save(event), 0L, 0L,  List.of());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "events", key = "#eventId")
    public EventFullDto getEventPrivate(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (!checkIsOwner(userId, event.getInitiator().getId())) {
            throw new EntityNotFoundException("Просматривать событие может только пользователь, добавивший его");
        }
        Map<Long, Long> confirmedRequests = getConfirmedRequests(List.of(event));
        Map<Long, Long> views = getEventViews(List.of(event));
        Map<Event, List<Comment>> comments = getComments(event);
        return eventMapper.eventToEventFullDto(event, confirmedRequests.getOrDefault(event.getId(), 0L),
                views.getOrDefault(event.getId(), 0L), comments.getOrDefault(event, List.of()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsPrivate(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User with id=%d was not found", userId));
        }
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "eventDate"));
        Page<Event> eventsPageable = eventRepository.findAllByInitiatorId(userId, pageRequest);
        List<Event> events = eventsPageable.getContent();
        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Long, Long> eventViews = getEventViews(events);
        List<EventShortDto> eventsWithRequests = new ArrayList<>();
        for (Event event : events) {
            Long views = eventViews.get(event.getId());
            EventShortDto eventDto = eventMapper.eventToEventShortDto(event,
                    confirmedRequests.get(event.getId()), views);
            eventsWithRequests.add(eventDto);
        }
        return eventsWithRequests;
    }

    @Override
    @Transactional
    @Cacheable(cacheNames = "events", key = "#eventId")
    public EventFullDto updateEventPrivate(UpdateEventUserRequest eventDto, Long userId, Long eventId) {
        Event eventToUpdate = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (eventToUpdate.getState() != null && EventState.PUBLISHED.equals(eventToUpdate.getState())) {
            throw new EventStatusException("Only pending or canceled events can be changed");
        }
        if (!checkIsOwner(userId, eventToUpdate.getInitiator().getId())) {
            throw new EntityNotFoundException("Обновлять событие может только пользователь, добавивший его");
        }
        if (eventDto.getEventDate() != null && !eventDto.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new EventStatusException("It is restricted to set event start time two hours before the event.");
        }
        Category category = null;
        if (eventDto.getCategory() != null) {
            category = categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Category with id=%d was not found",
                            eventDto.getCategory())));
        }
        if (eventDto.getStateAction() != null && UpdateEventUserRequest.StateAction.CANCEL_REVIEW.equals(eventDto.getStateAction())) {
            eventToUpdate.setState(EventState.CANCELED);
        }
        if (eventDto.getStateAction() != null && UpdateEventUserRequest.StateAction.SEND_TO_REVIEW.equals(eventDto.getStateAction())) {
            eventToUpdate.setState(EventState.PENDING);
        }
        eventMapper.updateFromDto(eventToUpdate, eventDto, category);
        Event event = eventRepository.save(eventToUpdate);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(List.of(event));
        Map<Long, Long> views = getEventViews(List.of(event));
        Map<Event, List<Comment>> comments = getComments(event);
        return eventMapper.eventToEventFullDto(event, confirmedRequests.getOrDefault(event.getId(), 0L),
                views.getOrDefault(event.getId(), 0L), comments.getOrDefault(event, List.of()));
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getEventRequestsPrivate(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (!checkIsOwner(userId, event.getInitiator().getId())) {
            throw new EntityNotFoundException("Просматривать запрос может только пользователь, добавивший его");
        }
        return requestMapper.participationRequestToRequestDto(requestRepository.findAllByEventId(eventId));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestPrivate(EventRequestStatusUpdateRequest requestDto, Long userId,
                                                               Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (!checkIsOwner(userId, event.getInitiator().getId())) {
            throw new EntityNotFoundException("Обновлять запрос может только пользователь, добавивший его");
        }
        List<ParticipationRequest> requestsToUpdate = requestRepository.findAllByIdIn(requestDto.getRequestIds());
        if ((event.getParticipantLimit() == 0) || !event.getRequestModeration()) {
            for (ParticipationRequest p : requestsToUpdate) {
                p.setStatus(RequestStatus.valueOf(requestDto.getStatus().toString()));
            }
            return requestMapper.requestsToEventRequestStatusUpdateResult(requestRepository.saveAll(requestsToUpdate));
        }
        if (isParticipantLimitAchieved(event)) {
            throw new EventStatusException("The participant limit has been reached");
        }
        Integer participantLimit = event.getParticipantLimit();
        List<ParticipationRequest> requests = requestRepository.saveAll(updateRequestStatus(eventId, requestsToUpdate,
                requestDto, participantLimit));
        return requestMapper.requestsToEventRequestStatusUpdateResult(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsAdmin(EventsAdminRequest eventsAdminRequest, Integer from, Integer size) {
        if ((eventsAdminRequest.getRangeEnd() != null && eventsAdminRequest.getRangeStart() != null)
                && (eventsAdminRequest.getRangeStart().isAfter(eventsAdminRequest.getRangeEnd()))) {
            throw new EventTimeException("EndDate cannot be before StartDate");
        }
        BooleanExpression conditions = getPredicate(eventsAdminRequest);
        PageRequest page = PageRequest.of(from / size, size);
        Page<Event> events = eventRepository.findAll(conditions, page);
        List<Event> events1 = events.getContent();
        Map<Long, Long> confirmedRequests = getConfirmedRequests(events1);
        Map<Long, Long> eventViews = getEventViews(events1);
        Map<Event, List<Comment>> comments = getComments(events1);

        List<EventFullDto> eventsFullDtos = new ArrayList<>();
        for (Event event : events1) {
            Long views = eventViews.get(event.getId());
            Long numberOfConfirmedRequests = confirmedRequests.get(event.getId());
            List<Comment> eventComments = comments.getOrDefault(event, List.of());
            EventFullDto eventDto = eventMapper.eventToEventFullDto(event, numberOfConfirmedRequests, views, eventComments);
            eventsFullDtos.add(eventDto);
        }
        eventsFullDtos.sort(Comparator.comparing(EventFullDto::getViews).reversed());
        return eventsFullDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsPublic(EventsAdminRequest eventsAdminRequest, Boolean onlyAvailable,
                                               SortParameter sort, Integer from, Integer size) {
        if ((eventsAdminRequest.getRangeEnd() != null && eventsAdminRequest.getRangeStart() != null)
                && (eventsAdminRequest.getRangeStart().isAfter(eventsAdminRequest.getRangeEnd()))) {
            throw new EventTimeException("EndDate cannot be before StartDate");
        }
        BooleanExpression conditions = getPredicate(eventsAdminRequest);
        PageRequest page = PageRequest.of(from / size, size);
        Page<Event> events = eventRepository.findAll(conditions, page);
        List<Event> events1 = events.getContent();
        Map<Long, Long> confirmedRequests = getConfirmedRequests(events1);
        Map<Long, Long> eventViews = getEventViews(events1);
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event : events1) {
            Long views = eventViews.get(event.getId());
            Long numberOfConfirmedRequests = confirmedRequests.get(event.getId());
            if (onlyAvailable) {
                if (numberOfConfirmedRequests >= event.getParticipantLimit()) {
                    continue;
                }
            }
            EventShortDto eventDto = eventMapper.eventToEventShortDto(event, numberOfConfirmedRequests, views);
            eventShortDtos.add(eventDto);
        }
        if (SortParameter.VIEWS.equals(sort)) {
            eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews).reversed());
        }
        if (SortParameter.EVENT_DATE.equals(sort)) {
            eventShortDtos.sort(Comparator.comparing(EventShortDto::getEventDate).reversed());
        }
        return eventShortDtos;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "events", key = "#eventId")
    public EventFullDto getEventPublic(Long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));
        Map<Long, Long> confirmedRequests = getConfirmedRequests(List.of(event));
        Map<Long, Long> views = getEventViews(List.of(event));
        Map<Event, List<Comment>> comments = getComments(event);
        return eventMapper.eventToEventFullDto(event, confirmedRequests.getOrDefault(event.getId(), 0L),
                views.getOrDefault(event.getId(), 0L), comments.getOrDefault(event, List.of()));
    }

    @Override
    @Transactional
    public EventFullDto updateEventAdmin(UpdateEventAdminRequest eventDto, Long eventId) {
        System.out.println(eventDto);
        Event eventToUpdate = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (eventDto.getEventDate() != null && !eventDto.getEventDate().isAfter(LocalDateTime.now().plusHours(1))) {
            throw new EventTimeException("It is restricted to set event start time one hour before the event.");
        }
        if (eventDto.getStateAction() != null) {
            if ((UpdateEventAdminRequest.StateAction.PUBLISH_EVENT.equals(eventDto.getStateAction()))) {
                if (!EventState.PENDING.equals(eventToUpdate.getState())) {
                    throw new EventStatusException(String.format("Cannot publish the event because it's not in the right state: %s",
                            eventToUpdate.getState()));
                }
                eventToUpdate.setState(EventState.PUBLISHED);
                eventToUpdate.setPublishedOn(LocalDateTime.now());
            }
        }
        if (eventDto.getStateAction() != null && UpdateEventAdminRequest.StateAction.REJECT_EVENT.equals(eventDto.getStateAction())) {
            if (EventState.PUBLISHED.equals(eventToUpdate.getState())) {
                throw new EventStatusException(String.format("Cannot publish the event because it's not in the right state: %s",
                        eventToUpdate.getState()));
            } else {
                eventToUpdate.setState(EventState.CANCELED);
            }
        }
        Category category = null;
        if (eventDto.getCategory() != null) {
            category = categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Category with id=%d was not found",
                            eventDto.getCategory())));
        }
        eventMapper.updateFromDto(eventToUpdate, eventDto, category);
        Event event = eventRepository.save(eventToUpdate);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(List.of(event));
        Map<Long, Long> views = getEventViews(List.of(event));
        Map<Event, List<Comment>> comments = getComments(event);
        return eventMapper.eventToEventFullDto(event, confirmedRequests.getOrDefault(event.getId(), 0L),
                views.getOrDefault(event.getId(), 0L), comments.getOrDefault(event, List.of()));
    }

    private boolean isParticipantLimitAchieved(Event event) {
        Long confirmedRequests = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        Integer participationLimit = event.getParticipantLimit();
        return (confirmedRequests >= participationLimit);
    }

    private List<ParticipationRequest> updateRequestStatus(Long eventId, List<ParticipationRequest> requests,
                                                           EventRequestStatusUpdateRequest requestDto, Integer participantLimit) {
        Long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        for (ParticipationRequest r : requests) {
            if (confirmedCount >= participantLimit) {
                r.setStatus(RequestStatus.REJECTED);
                continue;
            }
            if (RequestStatus.PENDING.equals(r.getStatus())) {
                r.setStatus(RequestStatus.valueOf(requestDto.getStatus().toString()));
                ++confirmedCount;
            }
        }
        return requests;
    }

    private boolean checkIsOwner(Long ownerId, Long userId) {
        return Objects.equals(ownerId, userId);
    }

    private Map<Long, Long> getConfirmedRequests(List<Event> events) {
        List<RequestShortCount> shorts = requestRepository.countByEventInAndStatus(events, RequestStatus.CONFIRMED);
        return requestRepository.countByEventInAndStatus(events, RequestStatus.CONFIRMED).stream()
                .collect(Collectors.toMap(RequestShortCount::getEventId, RequestShortCount::getCount));
    }

    private Map<Long, Long> getEventViews(List<Event> events) {
        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .collect(Collectors.toList());
        List<StatsDtoResponse> viewStats;
        List<LocalDateTime> dates = events.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
        if (!dates.isEmpty()) {
            viewStats = statisticsClient.getStats(dates.get(0), LocalDateTime.now(), uris, true);
        } else {
            viewStats = List.of();
        }
        return viewStats.stream()
                .filter(v -> !("/events".equals(v.getUri())))
                .collect(Collectors.toMap(
                        v -> Long.parseLong(String.valueOf(v.getUri().charAt(v.getUri().length() - 1))),
                        v -> v.getHits(),
                        (v1, v2) -> v1 + v2)
                );
    }

    private static BooleanExpression getPredicate(EventsAdminRequest request) {
        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();
        if (request.hasText()) {
            String text = request.getText();
            conditions.add((event.annotation.containsIgnoreCase(text))
                    .or(event.description.containsIgnoreCase(text)));
        }
        if (request.hasCategories() && (request.getCategories().get(0) != 0)) {
            conditions.add(event.category.id.in(request.getCategories()));
        }
        if (request.hasInitiators() && (request.getInitiators().get(0) != 0)) {
            conditions.add(event.initiator.id.in(request.getInitiators()));
        }
        if (request.hasPaid()) {
            conditions.add(event.paid.eq(request.getPaid()));
        }
        LocalDateTime rangeStart = request.getRangeStart() != null ? request.getRangeStart() : LocalDateTime.now();
        conditions.add(event.eventDate.goe(rangeStart));
        if (request.getRangeEnd() != null) {
            conditions.add(event.eventDate.loe(request.getRangeEnd()));
        }
        if (request.hasStates()) {
            conditions.add(QEvent.event.state.in(request.getStates()));
        }
        return conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
    }

    private Map<Event, List<Comment>> getComments(List<Event> events) {
        return commentRepository.findByEventIn(events, Sort.by(DESC, "createdOn")).stream()
                .collect(Collectors.groupingBy(Comment::getEvent));
    }

    private Map<Event, List<Comment>> getComments(Event event) {
        return commentRepository.findByEvent(event, Sort.by(DESC, "createdOn")).stream()
                .collect(Collectors.groupingBy(Comment::getEvent));
    }
}
