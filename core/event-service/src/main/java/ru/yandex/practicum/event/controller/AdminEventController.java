package ru.yandex.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contracts.dto.event.AdminEventSearchRequest;
import ru.yandex.practicum.contracts.dto.event.EventFullDto;
import ru.yandex.practicum.contracts.dto.event.UpdateEventAdminDto;
import ru.yandex.practicum.contracts.enums.EventState;
import ru.yandex.practicum.event.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEvents(
            @RequestParam(name = "users", required = false) List<Long> users,
            @RequestParam(name = "states", required = false) List<EventState> states,
            @RequestParam(name = "categories", required = false) List<Long> categories,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {

        return eventService.getEventsWithParamsByAdmin(
                AdminEventSearchRequest.fromParams(
                        users, states, categories, rangeStart, rangeEnd, from, size));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable(name = "eventId") Long eventId,
                                    @Valid @RequestBody UpdateEventAdminDto updateEventAdminDto) {
        return eventService.updateEvent(eventId, updateEventAdminDto);
    }
}
