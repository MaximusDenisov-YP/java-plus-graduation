package ru.practicum.ewm.main.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.dto.comment.CreateCommentDto;
import ru.practicum.ewm.main.entity.Comment;
import ru.practicum.ewm.main.entity.Event;
import ru.practicum.ewm.main.entity.User;
import ru.practicum.ewm.main.exception.CommentNotExistException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.mapper.CommentMapper;
import ru.practicum.ewm.main.repository.CommentRepository;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommentNotExistException("Not possible create Comment - " + "Does not exist User with Id " + userId));

        Comment commentFromDto = commentMapper.toComment(createCommentDto);

        commentFromDto.setEvent(event);
        commentFromDto.setOwner(user);
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