package ru.yandex.practicum.event.service.event;

import ru.yandex.practicum.contracts.dto.event.*;

import java.util.List;
import java.util.Set;

public interface EventService {

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEvents(Long userId, int from, int size);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserDto updateEventUserDto);

    EventFullDto getEventByUser(Long userId, Long eventId);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminDto updateEventAdminDto);

    EventFullDto getEvent(Long eventId, Long userId);

    List<EventFullDto> getEventsWithParamsByAdmin(AdminEventSearchRequest request);

    List<EventFullDto> getEventsWithParamsByUser(PublicEventSearchRequest request);

    EventFullDto getEventById(Long eventId);

    List<EventFullDto> getTopEvent(int count);

    boolean eventExists(Long eventId);

    boolean existsByCategoryId(Long categoryId);

    List<EventFullDto> getEventsByIds(Set<Long> ids);

    void likeEvent(long userId, long eventId);

    List<EventFullDto> getRecommendations(long userId, int size);

    List<EventFullDto> getSimilarEvents(long eventId, long userId, int size);
}