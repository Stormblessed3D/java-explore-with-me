package ru.practicum.controller.publicapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.StatisticsClient;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.ewm.StatsDtoRequest;
import ru.practicum.model.EventState;
import ru.practicum.model.SortParameter;
import ru.practicum.service.EventService;
import ru.practicum.util.EventsAdminRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventPublicController {
    private final EventService eventService;
    private final StatisticsClient statisticsClient;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEventsPublic(@RequestParam(value = "text", required = false) String text,
                                                               @RequestParam(value = "categories", required = false) List<Long> categories,
                                                               @RequestParam(value = "paid", required = false) Boolean paid,
                                                               @RequestParam(value = "rangeStart", required = false)
                                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                               @RequestParam(value = "rangeEnd", required = false)
                                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  LocalDateTime rangeEnd,
                                                               @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                                               @RequestParam(value = "sort", required = false) SortParameter sort,
                                                               @RequestParam(value = "from", defaultValue = "0")
                                                               @Min(value = 0) Integer from,
                                                               @RequestParam(value = "size", defaultValue = "10")
                                                               @Min(value = 1) Integer size,
                                                               HttpServletRequest request) {
        log.info("GET public запрос на получение событий");
        EventsAdminRequest eventsAdminRequest = EventsAdminRequest.builder()
                .states(List.of(EventState.PUBLISHED))
                .text(text)
                .paid(paid)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        statisticsClient.saveHit(new StatsDtoRequest("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now()));
        return ResponseEntity.ok(eventService.getEventsPublic(eventsAdminRequest, onlyAvailable, sort, from, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventPublic(@PathVariable @Positive Long id, HttpServletRequest request) {
        log.info("GET public запрос на получение подробной информации об опубликованном событии по id {}", id);
        statisticsClient.saveHit(new StatsDtoRequest("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now()));
        return ResponseEntity.ok(eventService.getEventPublic(id));
    }
}
