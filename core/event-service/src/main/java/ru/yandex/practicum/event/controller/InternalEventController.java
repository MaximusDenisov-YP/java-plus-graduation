package ru.yandex.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contracts.dto.event.EventFullDto;
import ru.yandex.practicum.contracts.dto.event.UpdateEventAdminDto;
import ru.yandex.practicum.event.service.event.EventService;

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

    @GetMapping("/event/exists")
    public boolean existsByCategoryId(@RequestParam Long categoryId) {
        return eventService.existsByCategoryId(categoryId);
    }

    @GetMapping("/event/{eventId}")
    public EventFullDto getEventById(@PathVariable("eventId") Long eventId) {
        return eventService.getEventById(eventId);
    }

    @GetMapping("/event")
    public List<EventFullDto> getEventsByIds(@RequestParam Set<Long> ids) {
        return eventService.getEventsByIds(ids);
    }

    @PutMapping("/admin/events/{eventId}")
    public EventFullDto updateAdminEvent(@PathVariable("eventId") long eventId, @RequestBody UpdateEventAdminDto dto) {
        return eventService.updateEvent(eventId, dto);
    }
}
