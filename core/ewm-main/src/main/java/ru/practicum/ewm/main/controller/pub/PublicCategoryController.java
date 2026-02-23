package ru.practicum.ewm.main.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.service.category.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAll(@RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {
        return categoryService.getAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getById(@PathVariable Long catId) {
        return categoryService.getById(catId);
    }
}
