package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.dto.CategoryDto;
import ru.practicum.model.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CategoryMapper {
    public static final CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    public abstract Category categoryDtoRequestToCategory(NewCategoryDto categoryDtoRequest);

    public abstract CategoryDto categoryToCategoryDto(Category category);

    public abstract List<CategoryDto> categoryToCategoryDto(List<Category> category);
}
