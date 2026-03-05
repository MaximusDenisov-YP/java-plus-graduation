package ru.yandex.practicum.requestservice.fallback.event;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.contracts.feign.events.EventsClient;

@FeignClient(
        name = "event-service",
        path = "/internal",
        fallbackFactory = EventsClientFallbackFactory.class
)
public interface EventsClientWithFallback extends EventsClient {
}