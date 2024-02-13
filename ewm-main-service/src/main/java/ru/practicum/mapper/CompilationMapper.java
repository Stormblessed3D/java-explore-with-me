package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class CompilationMapper {
    public abstract CompilationDto compilationToCompilationDto(Compilation compilation);

    public Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .build();
    }

    public abstract List<CompilationDto> compilationToCompilationDto(Iterable<Compilation> compilation);

    @Mapping(source = "events", target = "events")
    @Mapping(source = "compilationDto.pinned", target = "pinned")
    @Mapping(source = "compilationDto.title", target = "title")
    public abstract Compilation updateFromDto(@MappingTarget Compilation compilation, UpdateCompilationRequest compilationDto, Set<Event> events);
}
