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
import ru.yandex.practicum.contracts.dto.user.UserShortDto;
import ru.yandex.practicum.contracts.exception.CommentNotExistException;
import ru.yandex.practicum.contracts.exception.NotFoundException;
import ru.yandex.practicum.events.entity.Comment;
import ru.yandex.practicum.events.entity.Event;
import ru.yandex.practicum.events.fallback.users.UsersClientWithFallback;
import ru.yandex.practicum.events.mapper.CommentMapper;
import ru.yandex.practicum.events.repository.CommentRepository;
import ru.yandex.practicum.events.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UsersClientWithFallback usersClient;
    private final CommentMapper commentMapper;

    @Override
    public List<CommentDto> getEventComments(Long eventId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").descending());
        Page<Comment> comments = commentRepository.findAllByEventId(eventId, pageable);
        Map<Long, UserShortDto> ownersByIds = getUserShortDtoMapWithIds(comments.getContent());
        return comments.stream()
                .map(c -> commentMapper.toCommentDto(c, ownersByIds.get(c.getOwnerId())
                        .getName())).toList();
    }

    @Override
    @Transactional
    public CommentDto create(Long eventId, Long userId, CreateCommentDto createCommentDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CommentNotExistException("Not possible create Comment - " +
                        "Does not exist Event with Id " + eventId));
        if (usersClient.userExists(userId)) {
            throw new CommentNotExistException("Not possible create Comment - " + "Does not exist User with Id " + userId);
        }
        Comment commentFromDto = commentMapper.toComment(createCommentDto);

        commentFromDto.setEvent(event);
        commentFromDto.setOwnerId(userId);
        commentFromDto.setCreated(LocalDateTime.now());
        UserShortDto user = usersClient.getUserShort(userId);
        Comment comment = commentRepository.save(commentFromDto);
        return commentMapper.toCommentDto(comment, user.getName());
    }

    @Override
    @Transactional
    public CommentDto update(Long commentId, Long userId, CreateCommentDto createCommentDto) {
        Comment comment = commentRepository.findByIdAndOwnerId(commentId, userId)
                .orElseThrow(() -> new CommentNotExistException("Not possible update Comment - " +
                        "Does not exist comment with Id " + commentId + " for user with id " + userId));

        comment.setText(createCommentDto.getText());
        UserShortDto user = usersClient.getUserShort(userId);
        return commentMapper.toCommentDto(commentRepository.save(comment), user.getName());
    }

    @Override
    @Transactional
    public void delete(Long commentId, Long userId) {
        commentRepository.deleteByIdAndOwnerId(commentId, userId);
    }

    @Override
    public List<CommentDto> findCommentByText(String text) {
        List<Comment> allByTextIsLikeIgnoreCase = commentRepository.findAllByTextIsLikeIgnoreCase(text);
        Map<Long, UserShortDto> usersWithIds = getUserShortDtoMapWithIds(allByTextIsLikeIgnoreCase);
        return allByTextIsLikeIgnoreCase.stream()
                .map(comment ->
                        commentMapper.toCommentDto(comment, usersWithIds.get(comment.getOwnerId()).getName()))
                .toList();
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("комментарий не найден"));
        UserShortDto user = usersClient.getUserShort(comment.getOwnerId());
        return commentMapper.toCommentDto(comment, user.getName());
    }

    private Map<Long, UserShortDto> getUserShortDtoMapWithIds(List<Comment> comments) {
        List<Long> commentsUsersIds = comments.stream().map(Comment::getOwnerId).distinct().toList();
        Map<Long, UserShortDto> ownersByIds = new HashMap<>();
        usersClient.getUsersShort(commentsUsersIds)
                .forEach(user -> ownersByIds.put(user.getId(), user));
        return ownersByIds;
    }

}