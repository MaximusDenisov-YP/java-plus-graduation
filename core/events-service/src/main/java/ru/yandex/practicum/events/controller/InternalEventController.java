package ru.yandex.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contracts.dto.event.EventFullDto;
import ru.yandex.practicum.events.service.event.EventService;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalEventController {

    private final EventService eventService;

    @GetMapping("/event/{eventId}/exists")
    public boolean eventExists(@PathVariable Long eventId) {
        return eventService.eventExists(eventId);
    }

    @GetMapping("/event/{eventId}")
    public EventFullDto getEventById(@PathVariable("eventId") Long eventId) {
        return eventService.getEventById(eventId);
    }

    @GetMapping("/event")
    public List<EventFullDto> getEventsByIds(@RequestParam Set<Long> ids) {
        return eventService.getEventsByIds(ids);
    }

}
