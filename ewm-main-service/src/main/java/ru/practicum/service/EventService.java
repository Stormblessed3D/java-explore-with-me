package ru.practicum.service;

import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.model.SortParameter;
import ru.practicum.util.EventsAdminRequest;

import java.util.List;

public interface EventService {
    EventFullDto createEvent(NewEventDto eventDto, Long userId);

    EventFullDto getEventPrivate(Long userId, Long eventId);

    List<EventShortDto> getEventsPrivate(Long userId, Integer from, Integer size);

    EventFullDto updateEventPrivate(UpdateEventUserRequest eventDto, Long userId, Long eventId);

    List<ParticipationRequestDto> getEventRequestsPrivate(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestPrivate(EventRequestStatusUpdateRequest requestDto, Long userId, Long eventId);

    List<EventFullDto> getEventsAdmin(EventsAdminRequest eventsAdminRequest, Integer from, Integer size);

    EventFullDto updateEventAdmin(UpdateEventAdminRequest eventDto, Long eventId);

    List<EventShortDto> getEventsPublic(EventsAdminRequest eventsAdminRequest, Boolean onlyAvailable, SortParameter sort,
                                        Integer from, Integer size);

    EventFullDto getEventPublic(Long id);
}
