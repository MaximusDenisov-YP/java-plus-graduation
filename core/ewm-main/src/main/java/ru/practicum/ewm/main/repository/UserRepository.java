package ru.practicum.ewm.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
}