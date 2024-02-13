package ru.practicum.controller.adminapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> createCompilation(@RequestBody @Valid NewCompilationDto compilationDto) {
        log.info("POST admin запрос на добавление новой подборки {}", compilationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(compilationService.createCompilationAdmin(compilationDto));
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable @Positive Long compId) {
        log.info("DELETE admin запрос на удаление подборки с id {}", compId);
        compilationService.deleteCompilationAdmin(compId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@RequestBody @Valid UpdateCompilationRequest compilationDto,
                                                            @PathVariable @Positive Long compId) {
        log.info("PATCH admin запрос на обновление подборки с id {}, тело запроса {}", compId, compilationDto);
        return ResponseEntity.ok(compilationService.updateCompilationAdmin(compilationDto, compId));
    }
}
