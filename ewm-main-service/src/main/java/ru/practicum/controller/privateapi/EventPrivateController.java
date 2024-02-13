package ru.practicum.controller.privateapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@Controller
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping()
    public ResponseEntity<EventFullDto> createEvent(@RequestBody @Valid NewEventDto eventDto,
                                                    @PathVariable @Positive Long userId) {
        log.info("POST Запрос на добавление события {} пользователем с id {}", eventDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(eventDto, userId));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long eventId) {
        log.info("GET Запрос на получение события с id {} пользователем с id {}", eventId, userId);
        return ResponseEntity.ok(eventService.getEventPrivate(userId, eventId));
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(@PathVariable @Positive Long userId,
                                                         @RequestParam(value = "from", defaultValue = "0") @Min(value = 0) Integer from,
                                                         @RequestParam(value = "size", defaultValue = "10") @Min(value = 1) Integer size) {
        log.info("GET Запрос на получение событий пользователем {}, с параметрами from {} size {}", userId, from, size);
        return ResponseEntity.ok(eventService.getEventsPrivate(userId, from, size));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@RequestBody @Valid UpdateEventUserRequest eventDto,
                                                    @PathVariable(value = "userId") @Positive Long userId,
                                                    @PathVariable(value = "eventId") @Positive Long eventId) {
        log.info("PATCH Запрос на обновление события с id {} {} пользователем с id {}", eventId, eventDto, userId);
        return ResponseEntity.ok(eventService.updateEventPrivate(eventDto, userId, eventId));
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getEventRequests(@PathVariable @Positive Long userId,
                                                                          @PathVariable @Positive Long eventId) {
        log.info("GET Запрос на получение запросов на участие в событии {} пользователем {}", eventId, userId);
        return ResponseEntity.ok(eventService.getEventRequestsPrivate(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateEvent(@RequestBody @Valid EventRequestStatusUpdateRequest requestDto,
                                                                      @PathVariable(value = "userId") @Positive Long userId,
                                                                      @PathVariable(value = "eventId") @Positive Long eventId) {
        log.info("PATCH Запрос на изменение статуса заявок на участие {} в событии с id {} пользователем с id {}", requestDto, eventId, userId);
        return ResponseEntity.ok(eventService.updateRequestPrivate(requestDto, userId, eventId));
    }
}
