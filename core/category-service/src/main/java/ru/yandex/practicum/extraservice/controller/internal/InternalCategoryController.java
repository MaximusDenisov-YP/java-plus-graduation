package ru.yandex.practicum.extraservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contracts.dto.category.CategoryDto;
import ru.yandex.practicum.extraservice.service.category.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/category/{categoryId}")
    public CategoryDto getCategory(@PathVariable Long categoryId) {
        return categoryService.getById(categoryId);
    }

    @PostMapping("/category")
    List<CategoryDto> getCategoriesByIds(@RequestBody List<Long> categoriesIds) {
        return categoryService.getCategoriesByIds(categoriesIds);
    }

    @GetMapping("/category/{categoryId}/exists")
    public boolean categoryExists(@PathVariable Long categoryId) {
        return categoryService.categoryExists(categoryId);
    }
}
