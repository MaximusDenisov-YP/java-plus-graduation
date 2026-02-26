package ru.yandex.practicum.usersservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contracts.dto.user.NewUserRequest;
import ru.yandex.practicum.contracts.dto.user.UserDto;
import ru.yandex.practicum.usersservice.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(
            @RequestParam(required = false) @Valid List<Long> ids,
            @RequestParam(defaultValue = "0") @Valid int from,
            @RequestParam(defaultValue = "10") @Valid int size
    ) {
        log.info("Admin request: get users, ids={}, from={}, size={}", ids, from, size);
        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid NewUserRequest newUser) {
        log.info("Admin request: create user {}", newUser);
        return userService.createUser(newUser);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Admin request: delete user with id={}", userId);
        userService.deleteUser(userId);
    }
}