package ru.practicum.main.category.service;

import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getCategory(int from, int size);

    CategoryDto getByCategoryId(Long catId);

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(CategoryDto updateCategory, Long catId);
}
