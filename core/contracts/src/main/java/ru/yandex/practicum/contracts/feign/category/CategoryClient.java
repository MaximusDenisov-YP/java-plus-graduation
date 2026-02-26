package ru.yandex.practicum.contracts.feign.category;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.yandex.practicum.contracts.dto.category.CategoryDto;

@FeignClient(name = "extra-service", path = "/internal")
public interface CategoryClient {
    @PostMapping("/category/{categoryId}")
    CategoryDto getCategory(@PathVariable("categoryId") Long categoryId);

    @GetMapping("/category/{categoryId}/exists")
    boolean categoryExists(@PathVariable("categoryId") Long categoryId);
}
