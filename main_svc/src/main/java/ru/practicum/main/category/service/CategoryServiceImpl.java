package ru.practicum.main.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.Category;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.CategoryMapper;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getCategory(int from, int size) {
        log.info("Получен запрос на получения категорий {} , {}", from, size);

        var pageable = PageRequest.of(from, size);

        var page = categoryRepository.findAll(pageable);

        return page.getContent()
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getByCategoryId(Long catId) {
        log.info("Получен запрос на получения категорий по id: {}", catId);

        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с ID " + catId + " не найдено"));

        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.info("Получен запрос на создания новой категории");

        if (newCategoryDto.getName() == null || newCategoryDto.getName().isBlank()) {
            throw new BadRequestException("Имя категории не может быть пустым");
        }

        CategoryDto categoryDto = CategoryMapper.toCategoryDto(newCategoryDto);

        Category category = categoryRepository.findByName(categoryDto.getName());

        if (category != null) {
            throw new ConflictException("Запрещено добавлять не уникальные категории");
        }

        if (newCategoryDto.getName().length() > 50) {
            throw new BadRequestException("Имя категории не может быть длиннее 50 символов");
        }

        Category saveCat = CategoryMapper.toCategory(categoryDto);
        Category savedCategory = categoryRepository.save(saveCat);

        return CategoryMapper.toCategoryDto(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategories(Long catId) {

        log.info("Получен запрос на удаления категории id:{}", catId);

        categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категории с ID " + catId + " не найден"));

        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto updateCategory, Long catId) {
        log.info("Получен запрос на изменение категории ID: {}", catId);

        if (updateCategory.getName() == null || updateCategory.getName().isBlank()) {
            throw new BadRequestException("Имя категории не может быть пустым");
        }

        Category findCat = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категории с ID " + catId + " не найден"));

        if (findCat.getName().equals(updateCategory.getName())) {
            return CategoryMapper.toCategoryDto(findCat);
        }

        Category existing = categoryRepository.findByName(updateCategory.getName());
        if (existing != null) {
            throw new ConflictException("Запрещено добавлять не уникальные категории");
        }

        findCat.setName(updateCategory.getName());
        Category saveCat = categoryRepository.save(findCat);

        return CategoryMapper.toCategoryDto(saveCat);
    }

}
