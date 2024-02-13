package ru.practicum.controller.adminapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.model.EventState;
import ru.practicum.service.EventService;
import ru.practicum.util.EventsAdminRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(@RequestParam(value = "users", required = false) List<Long> userIds,
                                                        @RequestParam(value = "states", required = false) List<EventState> states,
                                                        @RequestParam(value = "categories", required = false) List<Long> categories,
                                                        @RequestParam(value = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  LocalDateTime rangeStart,
                                                        @RequestParam(value = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  LocalDateTime rangeEnd,
                                                        @RequestParam(value = "from", defaultValue = "0") @Min(value = 0) Integer from,
                                                        @RequestParam(value = "size", defaultValue = "10") @Min(value = 1) Integer size) {
        log.info("GET admin запрос на получение событий с userId: {}, состоянием: {}, категорией: {}, датой начала {}, " +
                "датой окончания {}", userIds, states, categories, rangeStart, rangeEnd);
        EventsAdminRequest eventsAdminRequest = EventsAdminRequest.builder()
                .initiators(userIds)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        return ResponseEntity.ok(eventService.getEventsAdmin(eventsAdminRequest, from, size));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@RequestBody @Valid UpdateEventAdminRequest eventDto,
                                                    @PathVariable @Positive Long eventId) {
        log.info("PATCH admin запрос на редактирование события с id {} администратором", eventId);
        return ResponseEntity.ok(eventService.updateEventAdmin(eventDto, eventId));
    }
}
