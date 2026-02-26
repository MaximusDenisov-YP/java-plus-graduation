package ru.yandex.practicum.events.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.yandex.practicum.contracts.dto.event.*;

import java.util.List;
import java.util.Set;

public interface EventService {

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEvents(Long userId, int from, int size);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserDto updateEventUserDto);

    EventFullDto getEventByUser(Long userId, Long eventId);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminDto updateEventAdminDto);

    EventFullDto getEvent(Long id, HttpServletRequest request);

    List<EventFullDto> getEventsWithParamsByAdmin(AdminEventSearchRequest request);

    List<EventFullDto> getEventsWithParamsByUser(PublicEventSearchRequest request, HttpServletRequest httpRequest);

    EventFullDto getEventById(Long eventId);

    List<EventFullDto> getTopEvent(int count);

    boolean eventExists(Long eventId);

    List<EventFullDto> getEventsByIds(Set<Long> ids);
}