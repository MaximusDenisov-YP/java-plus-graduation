package ru.yandex.practicum.events.fallback.category;


import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.contracts.feign.category.CategoryClient;

@FeignClient(
        name = "extra-service",
        path = "/internal",
        fallbackFactory = CategoryClientFallbackFactory.class
)
public interface CategoryClientWithFallback extends CategoryClient {
}