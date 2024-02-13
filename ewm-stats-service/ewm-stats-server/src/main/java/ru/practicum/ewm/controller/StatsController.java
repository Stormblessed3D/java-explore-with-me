package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.StatsDtoRequest;
import ru.practicum.ewm.StatsDtoResponse;
import ru.practicum.ewm.service.StatsService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/stats")
    public ResponseEntity<List<StatsDtoResponse>> getStats(@RequestParam(value = "start") String start,
                                                           @RequestParam(value = "end") String end,
                                                           @RequestParam(value = "uris") Optional<List<String>> uris,
                                                           @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        log.info("Запрос на получение статистики по параметрам: {}, {}, {}, {}.", start, end, uris, unique);
        return ResponseEntity.ok(statsService.getStats(start, end, uris, unique));
    }

    @PostMapping("/hit")
    public ResponseEntity<Void> createHit(@RequestBody @Valid StatsDtoRequest statsDtoRequest) {
        log.info("Запрос на добавление в статистику: {}.", statsDtoRequest);
        statsService.createHit(statsDtoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
