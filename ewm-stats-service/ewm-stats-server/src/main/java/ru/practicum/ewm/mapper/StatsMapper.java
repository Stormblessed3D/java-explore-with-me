package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.StatsDtoRequest;
import ru.practicum.ewm.model.Stats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public abstract class StatsMapper {
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Stats statsDtoRequestToStats(StatsDtoRequest statsDtoRequest) {
        Stats stats = new Stats();
        stats.setApp(statsDtoRequest.getApp());
        stats.setUri(statsDtoRequest.getUri());
        stats.setIp(statsDtoRequest.getIp());
        stats.setTimestamp(LocalDateTime.parse(statsDtoRequest.getTimestamp(), DATE_TIME_FORMATTER));
        return stats;
    }
}
