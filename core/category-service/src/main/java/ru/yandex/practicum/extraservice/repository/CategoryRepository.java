package ru.yandex.practicum.extraservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.extraservice.entity.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findAll(Pageable pageable);

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsByName(String name);

    boolean existsById(Long userId);

    List<Category> findByIdIn(List<Long> id);
}