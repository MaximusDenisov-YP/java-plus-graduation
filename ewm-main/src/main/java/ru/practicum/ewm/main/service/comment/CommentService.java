package ru.practicum.ewm.main.service.comment;

import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.dto.comment.CreateCommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> getEventComments(Long eventId, int from, int size);

    CommentDto create(Long eventId, Long userId, CreateCommentDto createCommentDto);

    CommentDto update(Long commentId, Long userId, CreateCommentDto createCommentDto);

    void delete(Long userId, Long commentId);

    List<CommentDto> findCommentByText(String text);

    CommentDto getCommentById(Long commentId);
}
