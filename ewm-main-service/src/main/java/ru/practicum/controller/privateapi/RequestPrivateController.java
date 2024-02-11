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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@Controller
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestPrivateController {
    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(@PathVariable @Positive Long userId) {
        log.info("GET Запрос на получение информации о заявках текущего пользователя id {}", userId);
        return ResponseEntity.ok(requestService.getRequestsPrivate(userId));
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createRequest(@PathVariable @Positive Long userId,
                                                                 @RequestParam(value = "eventId") Long eventId) {
        log.info("POST запрос на добавление запроса от пользователя {} на участие в событии c id {}", userId, eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.createRequestPrivate(userId, eventId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable @Positive Long userId,
                                                                 @PathVariable @Positive Long requestId) {
        log.info("PATCH запрос отмену своего запроса на участие в событии id {} пользователем с id {}", requestId, userId);
        return ResponseEntity.ok(requestService.cancelRequest(userId, requestId));
    }

}
