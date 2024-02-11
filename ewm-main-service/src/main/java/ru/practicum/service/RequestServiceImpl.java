package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.exception.RequestConstraintException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.RequestStatus;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsPrivate(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User with id=%d was not found", userId));
        }
        List<ParticipationRequest> requests = requestRepository.findAllByRequesterId(userId);
        return requestMapper.participationRequestToRequestDto(requests);
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequestPrivate(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%d was not found", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new RequestConstraintException("Нельзя добавить повторный запрос");
        }
        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new RequestConstraintException("Нельзя участвовать в неопубликованном событии");
        }
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new RequestConstraintException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        ParticipationRequest request = new ParticipationRequest(LocalDateTime.now().withNano(0), event, user);
        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        if (event.getParticipantLimit() != 0) {
            if (event.getRequestModeration()) {
                request.setStatus(RequestStatus.PENDING);
            } else {
                checkParticipantLimit(event);
                request.setStatus(RequestStatus.CONFIRMED);
            }
        }
        return requestMapper.participationRequestToRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Request with id=%d was not found", requestId)));
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User with id=%d was not found", userId));
        }
        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.participationRequestToRequestDto(requestRepository.save(request));
    }

    private Long checkParticipantLimit(Event event) {
        Long numberOfRequests = requestRepository.countByEventId(event.getId());
        if (numberOfRequests >= event.getParticipantLimit()) {
            throw new RequestConstraintException("У события достигнут лимит запросов на участие");
        }
        return numberOfRequests;
    }
}
