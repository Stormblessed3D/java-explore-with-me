package ru.practicum.ewm.service;

import ru.practicum.ewm.StatsDtoRequest;
import ru.practicum.ewm.StatsDtoResponse;

import java.util.List;
import java.util.Optional;

public interface StatsService {
    List<StatsDtoResponse> getStats(String start, String end, Optional<List<String>> uris,
                                    Boolean unique);

    void createHit(StatsDtoRequest statsDtoRequest);
}
