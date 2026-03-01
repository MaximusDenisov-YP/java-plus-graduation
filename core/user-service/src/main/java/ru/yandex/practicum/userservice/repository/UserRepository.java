package ru.yandex.practicum.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.userservice.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    boolean existsById(Long userId);
}