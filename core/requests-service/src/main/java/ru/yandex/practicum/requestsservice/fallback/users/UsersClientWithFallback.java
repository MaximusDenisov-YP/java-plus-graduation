package ru.yandex.practicum.requestsservice.fallback.users;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.contracts.feign.users.UsersClient;

@FeignClient(
        name = "users-service",
        path = "/internal",
        fallbackFactory = UsersClientFallbackFactory.class
)
public interface UsersClientWithFallback extends UsersClient {
}
