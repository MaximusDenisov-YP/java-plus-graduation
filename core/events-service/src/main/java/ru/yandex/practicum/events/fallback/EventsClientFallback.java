//package ru.yandex.practicum.events.fallback;
//
//import org.springframework.stereotype.Component;
//import ru.yandex.practicum.contract.dto.event.EventFullDto;
//import ru.yandex.practicum.contract.dto.event.EventShortDto;
//import ru.yandex.practicum.contract.dto.event.NewEventDto;
//import ru.yandex.practicum.contract.dto.event.UpdateEventAdminDto;
//import ru.yandex.practicum.contract.feign.events.EventsClient;
//
//import javax.naming.ServiceUnavailableException;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class EventsClientFallback implements EventsClient {
//
//    @Override
//    public List<EventShortDto> getPublicEvents(Map<String, Object> params) {
//        return null;
//    }
//
//    @Override
//    public EventFullDto getPublicEvent(long id) {
//        return null;
//    }
//
//    @Override
//    public EventFullDto addEvent(long userId, NewEventDto dto) {
//        return null;
//    }
//
//    @Override
//    public List<EventShortDto> getUserEvents(long userId, int from, int size) {
//        return null;
//    }
//
//    @Override
//    public List<EventFullDto> searchAdminEvents(Map<String, Object> params) {
//        return null;
//    }
//
//    @Override
//    public EventFullDto updateAdminEvent(long eventId, UpdateEventAdminDto dto) {
//        return null;
//    }
//
//    @Override
//    public Map<Long, EventShortDto> getEventsShortByIds(List<Long> ids) {
//        return Collections.emptyMap();
//    }
//
//    @Override
//    public Map<Long, EventMetaDto> getEventMetaByIds(List<Long> ids) {
//        return null;
//    }
//
//}