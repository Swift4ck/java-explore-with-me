package ru.practicum.main.category.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.main.category.Category;

@UtilityClass
public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setName(category.getName());
        categoryDto.setId(category.getId());
        return categoryDto;
    }

    public static Category toCategory(CategoryDto categoryDto) {
        Category category = new Category();

        category.setName(categoryDto.getName());
        category.setId(categoryDto.getId());
        return category;
    }

    public static CategoryDto toCategoryDto(NewCategoryDto category) {
        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setName(category.getName());
        return categoryDto;
    }

}
