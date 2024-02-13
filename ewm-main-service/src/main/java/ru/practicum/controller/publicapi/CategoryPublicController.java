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
import ru.practicum.dto.CategoryDto;
import ru.practicum.service.CategoryServiceImpl;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@Controller
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryPublicController {
    private final CategoryServiceImpl categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(@RequestParam(value = "from", defaultValue = "0")
                                                           @Min(value = 0) Integer from,
                                                           @RequestParam(value = "size", defaultValue = "10")
                                                           @Min(value = 1) Integer size) {
        log.info("GET public запрос на получение категорий");
        return ResponseEntity.ok(categoryService.getCategoriesPublic(from, size));
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable @Positive Long catId) {
        log.info("GET public запрос на получение категории с Id {}", catId);
        return ResponseEntity.ok(categoryService.getCategoryPublic(catId));
    }
}
