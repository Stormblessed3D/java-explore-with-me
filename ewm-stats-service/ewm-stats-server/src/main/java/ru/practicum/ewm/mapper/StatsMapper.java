package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.StatsDtoRequest;
import ru.practicum.ewm.model.Stats;

@Mapper(componentModel = "spring")
public abstract class StatsMapper {

    public abstract Stats statsDtoRequestToStats(StatsDtoRequest statsDtoRequest);
}
