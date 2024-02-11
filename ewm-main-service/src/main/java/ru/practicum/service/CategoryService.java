package ru.practicum.service;

import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategoryAdmin(NewCategoryDto categoryDto);

    void deleteCategoryAdmin(Long catId);

    CategoryDto updateCategoryAdmin(NewCategoryDto categoryDtoRequest, Long catId);

    List<CategoryDto> getCategoriesPublic(Integer from, Integer size);

    CategoryDto getCategoryPublic(Long catId);
}
