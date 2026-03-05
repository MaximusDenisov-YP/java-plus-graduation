package ru.yandex.practicum.extraservice.fallback.event;

import feign.FeignException;
import ru.yandex.practicum.contracts.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.contracts.dto.event.EventFullDto;
import ru.yandex.practicum.contracts.dto.event.UpdateEventAdminDto;
import ru.yandex.practicum.contracts.exception.NotFoundException;

import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class EventsClientFallbackFactory implements FallbackFactory<EventsClientWithFallback> {

    @Override
    public EventsClientWithFallback create(Throwable cause) {
        return new EventsClientWithFallback() {

            @Override
            public EventFullDto updateAdminEvent(long eventId, UpdateEventAdminDto dto) {
                logFailure("updateAdminEvent eventId=" + eventId, cause);

                if (cause instanceof FeignException.NotFound) {
                    throw new NotFoundException("Event not found: " + eventId);
                }
                throw new ServiceUnavailableException("event-service unavailable");
            }

            @Override
            public boolean eventExists(Long eventId) {
                logFailure("eventExists eventId=" + eventId, cause);

                if (cause instanceof FeignException.NotFound) {
                    return false;
                }
                throw new ServiceUnavailableException("event-service unavailable");
            }

            @Override
            public boolean existsByCategoryId(Long categoryId) {
                logFailure("existsByCategoryId categoryId=" + categoryId, cause);

                if (cause instanceof FeignException.NotFound) {
                    return false;
                }
                throw new ServiceUnavailableException("event-service unavailable");
            }

            @Override
            public EventFullDto getEventById(Long eventId) {
                logFailure("getEventById eventId=" + eventId, cause);

                if (cause instanceof FeignException.NotFound) {
                    throw new NotFoundException("Event not found: " + eventId);
                }
                throw new ServiceUnavailableException("event-service unavailable");
            }

            @Override
            public List<EventFullDto> getEventsByIds(Set<Long> ids) {
                logFailure("getEventsByIds ids=" + ids, cause);
                throw new ServiceUnavailableException("event-service unavailable");
            }
        };
    }

    private void logFailure(String method, Throwable cause) {
        log.error("Произошёл fallback EventClient в методе {}. cause={}", method, cause.toString(), cause);
    }
}