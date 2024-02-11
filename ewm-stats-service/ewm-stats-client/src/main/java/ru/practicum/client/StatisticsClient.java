package ru.practicum.client;

import ru.practicum.ewm.StatsDtoRequest;
import ru.practicum.ewm.StatsDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsClient {
    void saveHit(StatsDtoRequest statsDtoRequest);

    List<StatsDtoResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
