package ru.yandex.practicum.events.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.events.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByEventId(Long eventId, Pageable pageable);

    Optional<Comment> findByIdAndOwnerId(Long commentId, Long ownerId);

    void deleteByIdAndOwnerId(Long commentId, Long ownerId);

    List<Comment> findAllByTextIsLikeIgnoreCase(String text);

}
