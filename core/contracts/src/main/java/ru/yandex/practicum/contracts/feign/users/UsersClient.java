package ru.yandex.practicum.contracts.feign.users;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.contracts.dto.user.UserShortDto;

import java.util.List;

@FeignClient(name = "users-service", path = "/internal")
public interface UsersClient {
    @PostMapping("/users/short")
    List<UserShortDto> getUsersShort(@RequestBody List<Long> ids);

    @GetMapping("/users/{userId}")
    UserShortDto getUserShort(@PathVariable Long userId);

    @GetMapping("/users/{userId}/exists")
    boolean userExists(@PathVariable("userId") Long userId);
}
