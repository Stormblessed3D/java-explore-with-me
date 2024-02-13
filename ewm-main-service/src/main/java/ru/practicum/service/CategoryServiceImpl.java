package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.exception.CategoryViolationException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto createCategoryAdmin(NewCategoryDto categoryDto) {
        Category category = categoryMapper.categoryDtoRequestToCategory(categoryDto);
        return categoryMapper.categoryToCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    @CachePut(cacheNames = "categories", key = "#catId")
    public CategoryDto updateCategoryAdmin(NewCategoryDto categoryDtoRequest, Long catId) {
        Category categoryToUpdate = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Category with id=%d was not found", catId)));
        if (categoryDtoRequest.getName() != null && !categoryDtoRequest.getName().isBlank()) {
            categoryToUpdate.setName(categoryDtoRequest.getName());
        }
        Category category = categoryRepository.save(categoryToUpdate);
        return categoryMapper.categoryToCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getCategoriesPublic(Integer from, Integer size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Category> categories = categoryRepository.findAll(pageRequest);
        return categoryMapper.categoryToCategoryDto(categories.getContent());
    }

    @Override
    public CategoryDto getCategoryPublic(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Category with id=%d was not found", catId)));
        return categoryMapper.categoryToCategoryDto(category);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "categories", key = "#catId")
    public void deleteCategoryAdmin(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new EntityNotFoundException(String.format("Category with id=%d was not found", catId));
        }
        Long countEventWithCategory = eventRepository.countByCategory(catId);
        if (countEventWithCategory > 0) {
            throw new CategoryViolationException(String.format("The category is not empty", catId));
        }
        categoryRepository.deleteById(catId);
    }
}
