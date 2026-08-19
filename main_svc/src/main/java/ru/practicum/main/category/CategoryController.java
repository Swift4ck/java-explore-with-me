package ru.practicum.main.category;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.service.CategoryService;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;

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

    @DeleteMapping("/admin/categories/{catId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long catId) {
        categoryService.deleteCategories(catId);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/admin/categories")
//    public CategoryDto createCategory(@RequestBody NewCategoryDto newCategoryDto) {
//
//    }
//
//    @PatchMapping("/admin/categories/{catId}")
//    public CategoryDto updateCategory(@PathVariable Long catId, @RequestBody CategoryDto categoryDto) {
//
//    }
//
//    @PostMapping("/admin/compilations")
//    public CompilationDto createCompilation(@RequestBody NewCompilationDto newCompilationDto) {
//
//    }
//
//    @PatchMapping("/admin/compilations/{compId}")
//    public CompilationDto updateCompilation(@PathVariable Long compId, @RequestBody UpdateCompilationRequest updateRequest) {
//
//    }

}
