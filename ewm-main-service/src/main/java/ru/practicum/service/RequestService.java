package ru.practicum.service;

import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequestsPrivate(Long userId);

    ParticipationRequestDto createRequestPrivate(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
