package ru.practicum.controller.publicapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.CompilationDto;
import ru.practicum.service.CompilationService;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilations(@RequestParam(value = "pinned") Optional<Boolean> pinned,
                                                                @RequestParam(value = "from", defaultValue = "0") @Min(value = 0) Integer from,
                                                                @RequestParam(value = "size", defaultValue = "10") @Min(value = 1) Integer size) {
        log.info("GET Запрос на получение подборок событий с параметрами pinned {} from {} size {}", pinned, from, size);
        return ResponseEntity.ok(compilationService.getCompilations(pinned, from, size));
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable(value = "compId") @Positive Long compId) {
        log.info("GET Запрос на получение подборки событий с id {}", compId);
        return ResponseEntity.ok(compilationService.getCompilation(compId));
    }
}
