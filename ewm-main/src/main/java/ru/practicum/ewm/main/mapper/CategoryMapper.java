package ru.practicum.ewm.main.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.dto.category.NewCategoryDto;
import ru.practicum.ewm.main.dto.category.UpdateCategoryDto;
import ru.practicum.ewm.main.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toCategoryDto(Category category);

    Category toEntity(CategoryDto categoryDto);

    Category toCategory(NewCategoryDto newCategoryDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategory(UpdateCategoryDto updateCategoryDto, @MappingTarget Category category);
}
