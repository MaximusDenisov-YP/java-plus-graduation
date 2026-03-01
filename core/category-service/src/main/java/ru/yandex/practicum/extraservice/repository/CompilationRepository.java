package ru.yandex.practicum.extraservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.extraservice.entity.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
}