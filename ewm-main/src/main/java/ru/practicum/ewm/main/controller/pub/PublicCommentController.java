package ru.practicum.ewm.main.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.dto.event.EventFullDto;
import ru.practicum.ewm.main.service.comment.CommentService;
import ru.practicum.ewm.main.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PublicCommentController {

    private final CommentService commentService;
    private final EventService eventService;

    @GetMapping("/{eventId}/comments")
    public List<CommentDto> getEventComments(@PathVariable(name = "eventId") Long eventId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        return commentService.getEventComments(eventId, from, size);
    }

    @GetMapping("/comments/top")
    public List<EventFullDto> getTopEvents(@RequestParam(name = "count", defaultValue = "5") int count) {
        return eventService.getTopEvent(count);
    }

    @GetMapping("/comments/{commentId}")
    public CommentDto getCommentById(@PathVariable(name = "commentId") Long commentId) {
        return commentService.getCommentById(commentId);
    }

}