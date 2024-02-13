package ru.practicum.client;

import ru.practicum.ewm.StatsDtoResponse;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsClient {
    void saveHit(HttpServletRequest request);

    List<StatsDtoResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
