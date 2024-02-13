package ru.practicum.service;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;

import java.util.List;
import java.util.Optional;

public interface CompilationService {
    List<CompilationDto> getCompilations(Optional<Boolean> pinned, Integer from, Integer size);

    CompilationDto getCompilation(Long compId);

    CompilationDto createCompilationAdmin(NewCompilationDto compilationDto);

    void deleteCompilationAdmin(Long compId);

    CompilationDto updateCompilationAdmin(UpdateCompilationRequest compilationDto, Long compId);
}
