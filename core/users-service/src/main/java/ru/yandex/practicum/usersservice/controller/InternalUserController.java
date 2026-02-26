package ru.yandex.practicum.usersservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contracts.dto.user.UserShortDto;
import ru.yandex.practicum.usersservice.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalUserController {

    private final UserService userService;

    @PostMapping("/users/short")
    List<UserShortDto> getUsersShort(@RequestBody List<Long> ids) {
        return userService.getUsersShort(ids);
    }

    @GetMapping("/users/{userId}")
    UserShortDto getUserShort(@PathVariable("userId") Long userId) {
        return userService.getUserShort(userId);
    }

    @GetMapping("/users/{userId}/exists")
    boolean userExists(@PathVariable("userId") Long userId) {
        return userService.userExists(userId);
    }
}
