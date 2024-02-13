package ru.practicum.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.client.StatisticsClient;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.ewm.StatsDtoResponse;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class StatisticsSetter {
    @Autowired
    private static StatisticsClient statisticsClient;
    @Autowired
    private static EventMapper eventMapper;

    private StatisticsSetter() {
    }

    public static CompilationDto setConfirmedReviewsAndViews(List<Event> events, Map<Long, Long> confirmedRequests,
                                                              CompilationDto compilationDto) {
        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .collect(Collectors.toList());
        List<StatsDtoResponse> viewStats;
        List<LocalDateTime> dates = events.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
        if (!dates.isEmpty()) {
            viewStats = statisticsClient.getStats(dates.get(0), LocalDateTime.now(), uris, true);
        } else {
            viewStats = List.of();
        }
        Map<Long, Long> eventViews = viewStats.stream()
                .filter(v -> !("/events".equals(v.getUri())))
                .collect(Collectors.toMap(
                        v -> Long.parseLong(String.valueOf(v.getUri().charAt(v.getUri().length() - 1))),
                        v -> v.getHits(),
                        (first, second) -> first + second)
                );
        List<EventShortDto> eventsWithRequestsAndViews = new ArrayList<>();
        if (!events.isEmpty()) {
            for (Event event : events) {
                Long views = eventViews.get(event.getId());
                Long confirmedRequestsCount = confirmedRequests.get(event.getId());
                EventShortDto eventDto = eventMapper.eventToEventShortDto(event, confirmedRequestsCount, views);
                eventsWithRequestsAndViews.add(eventDto);
            }
        }
        compilationDto.setEvents(new HashSet<>(eventsWithRequestsAndViews));
        return compilationDto;
    }
}
