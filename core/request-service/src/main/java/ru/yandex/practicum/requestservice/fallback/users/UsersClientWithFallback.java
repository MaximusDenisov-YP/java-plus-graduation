package ru.yandex.practicum.requestservice.fallback.users;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.contracts.feign.users.UsersClient;

@FeignClient(
        name = "user-service",
        path = "/internal",
        fallbackFactory = UsersClientFallbackFactory.class
)
public interface UsersClientWithFallback extends UsersClient {
}
