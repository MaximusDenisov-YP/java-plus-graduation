package ru.yandex.practicum.contracts.feign.category;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.contracts.dto.category.CategoryDto;

import java.util.List;

@FeignClient(name = "category-service", path = "/internal")
public interface CategoryClient {
    @GetMapping("/category/{categoryId}")
    CategoryDto getCategory(@PathVariable("categoryId") Long categoryId);

    @PostMapping("/category")
    List<CategoryDto> getCategoriesByIds(@RequestBody List<Long> categoriesIds);

    @GetMapping("/category/{categoryId}/exists")
    boolean categoryExists(@PathVariable Long categoryId);
}
