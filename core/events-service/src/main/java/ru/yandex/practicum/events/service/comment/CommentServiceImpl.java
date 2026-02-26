package ru.yandex.practicum.events.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.contracts.dto.comment.CommentDto;
import ru.yandex.practicum.contracts.dto.comment.CreateCommentDto;
import ru.yandex.practicum.contracts.exception.CommentNotExistException;
import ru.yandex.practicum.contracts.exception.NotFoundException;
import ru.yandex.practicum.contracts.feign.users.UsersClient;
import ru.yandex.practicum.events.entity.Comment;
import ru.yandex.practicum.events.entity.Event;
import ru.yandex.practicum.events.mapper.CommentMapper;
import ru.yandex.practicum.events.repository.CommentRepository;
import ru.yandex.practicum.events.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UsersClient usersClient;
    private final CommentMapper commentMapper;

    @Override
    public List<CommentDto> getEventComments(Long eventId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").descending());
        Page<Comment> comments = commentRepository.findAllByEventId(eventId, pageable);

        return comments.getContent()
                .stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto create(Long eventId, Long userId, CreateCommentDto createCommentDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CommentNotExistException("Not possible create Comment - " +
                        "Does not exist Event with Id " + eventId));
        if (!usersClient.userExists(userId)) {
            throw new CommentNotExistException("Not possible create Comment - " + "Does not exist User with Id " + userId);
        }
        Comment commentFromDto = commentMapper.toComment(createCommentDto);

        commentFromDto.setEvent(event);
        commentFromDto.setOwnerId(userId);
        commentFromDto.setCreated(LocalDateTime.now());

        Comment comment = commentRepository.save(commentFromDto);
        return commentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto update(Long commentId, Long userId, CreateCommentDto createCommentDto) {
        Comment comment = commentRepository.findByIdAndOwnerId(commentId, userId)
                .orElseThrow(() -> new CommentNotExistException("Not possible update Comment - " +
                        "Does not exist comment with Id " + commentId + " for user with id " + userId));

        comment.setText(createCommentDto.getText());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void delete(Long commentId, Long userId) {
        commentRepository.deleteByIdAndOwnerId(commentId, userId);
    }

    @Override
    public List<CommentDto> findCommentByText(String text) {
        List<Comment> allByTextIsLikeIgnoreCase = commentRepository.findAllByTextIsLikeIgnoreCase(text);
        return allByTextIsLikeIgnoreCase.stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("комментарий не найден"));
        return commentMapper.toCommentDto(comment);
    }

}