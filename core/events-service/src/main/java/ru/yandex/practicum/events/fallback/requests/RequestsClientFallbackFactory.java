package ru.yandex.practicum.events.fallback.requests;

import feign.FeignException;
import ru.yandex.practicum.contracts.exception.ConflictException;
import ru.yandex.practicum.contracts.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.cloud.openfeign.FallbackFactory;
import ru.yandex.practicum.contracts.dto.event.UpdateParticipationRequestListDto;
import ru.yandex.practicum.contracts.dto.request.ParticipationRequestDto;
import ru.yandex.practicum.contracts.dto.request.UpdateParticipationRequestDto;
import ru.yandex.practicum.contracts.exception.NotFoundException;

import java.util.List;

@Component
@Slf4j
public class RequestsClientFallbackFactory implements FallbackFactory<RequestsClientWithFallback> {

    @Override
    public RequestsClientWithFallback create(Throwable cause) {
        return new RequestsClientWithFallback() {

            @Override
            public List<ParticipationRequestDto> getUserRequestsByEventId(Long userId, Long eventId) {
                logFailure("getUserRequestsByEventId userId=" + userId + ", eventId=" + eventId, cause);

                if (cause instanceof FeignException.NotFound) {
                    throw new NotFoundException("Requests not found for userId=" + userId + ", eventId=" + eventId);
                }
                throw new ServiceUnavailableException("requests-service unavailable");
            }

            @Override
            public UpdateParticipationRequestListDto updateUserRequestsByEventId(Long userId, Long eventId, UpdateParticipationRequestDto updateDto) {
                logFailure("updateUserRequestsByEventId userId=" + userId + ", eventId=" + eventId, cause);

                if (cause instanceof FeignException.NotFound) {
                    throw new NotFoundException("Requests not found for update userId=" + userId + ", eventId=" + eventId);
                }
                if (cause instanceof FeignException.Conflict) {
                    throw new ConflictException("Conflict exception in requests-service");
                }
                throw new ServiceUnavailableException("requests-service unavailable");
            }
        };
    }

    private void logFailure(String method, Throwable cause) {
        log.error("Произошёл fallback RequestsClient в методе {}. cause={}", method, cause.toString(), cause);
    }
}