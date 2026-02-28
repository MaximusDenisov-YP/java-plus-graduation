package ru.yandex.practicum.extraservice.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.contracts.dto.category.CategoryDto;
import ru.yandex.practicum.contracts.dto.category.NewCategoryDto;
import ru.yandex.practicum.contracts.dto.category.UpdateCategoryDto;
import ru.yandex.practicum.contracts.exception.CategoryNotExistException;
import ru.yandex.practicum.contracts.exception.ConflictException;
import ru.yandex.practicum.contracts.feign.events.EventsClient;
import ru.yandex.practicum.extraservice.entity.Category;
import ru.yandex.practicum.extraservice.mapper.CategoryMapper;
import ru.yandex.practicum.extraservice.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventsClient eventsClient;
    private final CategoryMapper categoryMapper;

    @Override
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotExistException("Category with id=" + id + " was not found");
        }

        if (eventsClient.existsByCategoryId(id)) {
            throw new ConflictException("Cannot delete category with id=" + id + " because it has linked events");
        }

        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("name").descending());
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.getContent()
                .stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() ->
                new CategoryNotExistException("Category with id=" + id + " was not found"));

        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        try {
            Category category = categoryMapper.toCategory(newCategoryDto);

            Category newCategory = categoryRepository.saveAndFlush(category);

            return categoryMapper.toCategoryDto(newCategory);

        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(
                    "Category with name '" + newCategoryDto.getName() + "' already exists"
            );
        }
    }


    @Override
    public CategoryDto updateCategory(Long id, UpdateCategoryDto updateCategoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotExistException("Category with id=" + id + " was not found"));

        if (updateCategoryDto.getName() != null &&
                !updateCategoryDto.getName().equals(category.getName())) {

            boolean nameExists = categoryRepository.existsByNameAndIdNot(updateCategoryDto.getName(), id);
            if (nameExists) {
                throw new ConflictException("Category name already exists");
            }
        }

        categoryMapper.updateCategory(updateCategoryDto, category);
        Category updateCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(updateCategory);
    }

    @Override
    public boolean categoryExists(Long id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public List<CategoryDto> getCategoriesByIds(List<Long> ids) {
        return categoryRepository.findByIdIn(ids).stream().map(categoryMapper::toCategoryDto).toList();
    }
}