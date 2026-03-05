package ru.yandex.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contracts.dto.event.EventFullDto;
import ru.yandex.practicum.contracts.dto.event.PublicEventSearchRequest;
import ru.yandex.practicum.contracts.enums.SortValue;
import ru.yandex.practicum.event.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEvents(
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "categories", required = false) List<Long> categories,
            @RequestParam(name = "paid", required = false) Boolean paid,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "onlyAvailable", required = false) Boolean onlyAvailable,
            @RequestParam(name = "sort", required = false) SortValue sort,
            @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            HttpServletRequest request) {

        PublicEventSearchRequest searchRequest = PublicEventSearchRequest.fromParams(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        return eventService.getEventsWithParamsByUser(searchRequest, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id,
                                 HttpServletRequest request) {
        return eventService.getEvent(id, request);
    }
}
