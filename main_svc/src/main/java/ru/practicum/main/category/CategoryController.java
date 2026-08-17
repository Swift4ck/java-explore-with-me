package ru.practicum.main.category;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.service.CategoryService;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public List<CategoryDto> getCategory(@RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        return categoryService.getCategory(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getByCategoryId(@PathVariable Long catId) {
        return categoryService.getByCategoryId(catId);
    }

    @PostMapping("/admin/categories")
    public CategoryDto createCategory(@RequestBody NewCategoryDto newCategoryDto) {
        return categoryService.createCategory(newCategoryDto);
    }


    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto updateCategory(@RequestBody CategoryDto updateCategory, @PathVariable Long catId) {
        return categoryService.updateCategory(updateCategory, catId);
    }


}
