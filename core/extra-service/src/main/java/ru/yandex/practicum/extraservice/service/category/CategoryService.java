package ru.yandex.practicum.extraservice.service.category;

import ru.yandex.practicum.contracts.dto.category.CategoryDto;
import ru.yandex.practicum.contracts.dto.category.NewCategoryDto;
import ru.yandex.practicum.contracts.dto.category.UpdateCategoryDto;

import java.util.List;

public interface CategoryService {

    void delete(Long id);

    List<CategoryDto> getAll(int from, int size);

    CategoryDto getById(Long id);

    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long id, UpdateCategoryDto updateCategoryDto);

    boolean categoryExists(Long id);
}
