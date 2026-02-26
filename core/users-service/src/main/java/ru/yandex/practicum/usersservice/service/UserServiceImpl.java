package ru.yandex.practicum.usersservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.contracts.dto.user.NewUserRequest;
import ru.yandex.practicum.contracts.dto.user.UserDto;
import ru.yandex.practicum.contracts.dto.user.UserShortDto;
import ru.yandex.practicum.contracts.exception.ConflictException;
import ru.yandex.practicum.contracts.exception.UserNotExistException;
import ru.yandex.practicum.usersservice.entity.User;
import ru.yandex.practicum.usersservice.mapper.UserMapper;
import ru.yandex.practicum.usersservice.repository.UserRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<User> users;

        if (ids == null || ids.isEmpty()) {
            Page<User> pageResult = repository.findAll(page);
            users = pageResult.getContent();
        } else {
            users = repository.findAllById(ids)
                    .stream()
                    .sorted(Comparator.comparingLong(User::getId))
                    .skip(from)
                    .limit(size)
                    .toList();
        }

        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }


    @Override
    public UserDto createUser(NewUserRequest newUser) {
        String email = newUser.getEmail();
        if (repository.existsByEmail(email)) {
            throw new ConflictException("User with email=%s already exists.".formatted(email));
        }

        User user = userMapper.toEntity(newUser);
        return userMapper.toDto(repository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        if (!repository.existsById(userId)) {
            throw new UserNotExistException("User with id=%d not found.".formatted(userId));
        }

        repository.deleteById(userId);
    }

    @Override
    public List<UserShortDto> getUsersShort(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return repository.findAllById(ids).stream()
                .map(userMapper::toUserShortDto)
                .toList();
    }

    @Override
    public UserShortDto getUserShort(Long id) {
        return userMapper.toUserShortDto(repository.findById(id)
                .orElseThrow(() -> new UserNotExistException("User with id=%d not found.".formatted(id))));
    }

    @Override
    public boolean userExists(Long id) {
        return repository.existsById(id);
    }
}