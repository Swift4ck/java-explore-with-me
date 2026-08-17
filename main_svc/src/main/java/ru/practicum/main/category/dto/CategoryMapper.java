package ru.practicum.main.category.dto;

import ru.practicum.main.category.Category;

public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setName(category.getName());
        return categoryDto;
    }

    public static Category toCategory(CategoryDto categoryDto) {
        Category category = new Category();

        category.setName(categoryDto.getName());
        return category;
    }

    public static CategoryDto toCategoryDto(NewCategoryDto category) {
        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setName(category.getName());
        return categoryDto;
    }

}
