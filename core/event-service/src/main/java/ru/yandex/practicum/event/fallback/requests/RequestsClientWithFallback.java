package ru.yandex.practicum.event.fallback.requests;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.contracts.feign.requests.RequestsClient;

@FeignClient(
        name = "request-service",
        path = "/internal",
        fallbackFactory = RequestsClientFallbackFactory.class
)
public interface RequestsClientWithFallback extends RequestsClient {
}