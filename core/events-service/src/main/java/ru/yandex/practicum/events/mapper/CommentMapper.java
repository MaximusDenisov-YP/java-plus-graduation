package ru.yandex.practicum.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.contracts.dto.comment.CommentDto;
import ru.yandex.practicum.contracts.dto.comment.CreateCommentDto;
import ru.yandex.practicum.events.entity.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    // TODO: Передавать ownerName в параметры метода
    @Mapping(source = "name", target = "ownerName")
    @Mapping(source = "comment.event.id", target = "event")
    CommentDto toCommentDto(Comment comment, String name);

    Comment toComment(CreateCommentDto createCommentDto);
}