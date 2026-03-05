package ru.yandex.practicum.userservice.service;

import ru.yandex.practicum.contracts.dto.user.NewUserRequest;
import ru.yandex.practicum.contracts.dto.user.UserDto;
import ru.yandex.practicum.contracts.dto.user.UserShortDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto createUser(NewUserRequest newUser);

    void deleteUser(Long userId);

    List<UserShortDto> getUsersShort(List<Long> ids);

    UserShortDto getUserShort(Long userId);

    boolean userExists(Long userId);
}