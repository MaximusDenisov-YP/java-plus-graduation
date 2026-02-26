package ru.yandex.practicum.extraservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.contracts.dto.category.CategoryDto;
import ru.yandex.practicum.extraservice.service.category.CategoryService;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/category/{categoryId}")
    public CategoryDto getCategory(@PathVariable("categoryId") Long categoryId) {
        return categoryService.getById(categoryId);
    }

    @GetMapping("/category/{categoryId}/exists")
    public boolean categoryExists(@PathVariable("categoryId") Long categoryId) {
        return categoryService.categoryExists(categoryId);
    }
}
