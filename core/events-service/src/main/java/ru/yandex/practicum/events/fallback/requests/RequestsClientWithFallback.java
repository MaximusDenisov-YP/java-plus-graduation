package ru.yandex.practicum.events.fallback.requests;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.contracts.feign.requests.RequestsClient;

@FeignClient(
        name = "requests-service",
        path = "/internal",
        fallbackFactory = RequestsClientFallbackFactory.class
)
public interface RequestsClientWithFallback extends RequestsClient {
}