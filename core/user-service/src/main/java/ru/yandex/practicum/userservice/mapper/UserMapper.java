package ru.yandex.practicum.userservice.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.contracts.dto.user.NewUserRequest;
import ru.yandex.practicum.contracts.dto.user.UserDto;
import ru.yandex.practicum.contracts.dto.user.UserShortDto;
import ru.yandex.practicum.userservice.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(NewUserRequest dto);

    UserShortDto toUserShortDto(User user);
}