package ru.yandex.practicum.events.service.comment;


import ru.yandex.practicum.contracts.dto.comment.CommentDto;
import ru.yandex.practicum.contracts.dto.comment.CreateCommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> getEventComments(Long eventId, int from, int size);

    CommentDto create(Long eventId, Long userId, CreateCommentDto createCommentDto);

    CommentDto update(Long commentId, Long userId, CreateCommentDto createCommentDto);

    void delete(Long userId, Long commentId);

    List<CommentDto> findCommentByText(String text);

    CommentDto getCommentById(Long commentId);
}
