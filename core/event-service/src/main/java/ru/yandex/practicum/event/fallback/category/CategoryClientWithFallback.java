package ru.yandex.practicum.event.fallback.category;


import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.contracts.feign.category.CategoryClient;

@FeignClient(
        name = "category-service",
        path = "/internal",
        fallbackFactory = CategoryClientFallbackFactory.class
)
public interface CategoryClientWithFallback extends CategoryClient {
}