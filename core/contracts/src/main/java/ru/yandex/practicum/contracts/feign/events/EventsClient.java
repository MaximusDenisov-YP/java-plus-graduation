package ru.yandex.practicum.contracts.feign.events;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contracts.dto.event.EventFullDto;
import ru.yandex.practicum.contracts.dto.event.UpdateEventAdminDto;

import java.util.List;
import java.util.Set;

@FeignClient(name = "events-service", path = "/internal")
public interface EventsClient {

    @PutMapping("/admin/events/{eventId}")
    EventFullDto updateAdminEvent(@PathVariable("eventId") long eventId, @RequestBody UpdateEventAdminDto dto);

    // --- Internal endpoints (чтобы не было N+1) ---

    @GetMapping("/event/{eventId}/exists")
    boolean eventExists(@PathVariable("eventId") Long eventId);

    @GetMapping("/event/exists")
    boolean existsByCategoryId(@RequestParam Long categoryId);

    @GetMapping("/event/{eventId}")
    EventFullDto getEventById(@PathVariable("eventId") Long eventId);

    @GetMapping("/event")
    List<EventFullDto> getEventsByIds(@RequestParam Set<Long> ids);
}