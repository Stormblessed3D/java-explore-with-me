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
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.dto.CategoryDto;
import ru.practicum.service.CategoryServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryAdminController {
    private final CategoryServiceImpl categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid NewCategoryDto categoryDto) {
        log.info("Запрос на добавление категории: {}", categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategoryAdmin(categoryDto));
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable @Positive Long catId) {
        log.info("Запрос на удаление категории с id {}", catId);
        categoryService.deleteCategoryAdmin(catId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@RequestBody @Valid NewCategoryDto categoryDtoRequest,
                                                      @PathVariable @Positive Long catId) {
        return ResponseEntity.ok(categoryService.updateCategoryAdmin(categoryDtoRequest, catId));
    }
}
