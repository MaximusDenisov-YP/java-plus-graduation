package ru.yandex.practicum.requestsservice.fallback.users;

import feign.FeignException;
import ru.yandex.practicum.contracts.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.contracts.dto.user.UserShortDto;
import ru.yandex.practicum.contracts.exception.NotFoundException;

import java.util.List;

@Component
@Slf4j
public class UsersClientFallbackFactory implements FallbackFactory<UsersClientWithFallback> {

    @Override
    public UsersClientWithFallback create(Throwable cause) {

        return new UsersClientWithFallback() {

            @Override
            public List<UserShortDto> getUsersShort(List<Long> ids) {
                logFailure("getUsersShort", cause);
                throw mapToDomainException(cause);
            }

            @Override
            public UserShortDto getUserShort(Long userId) {
                logFailure("getUserShort userId=" + userId, cause);
                throw mapToDomainException(cause);
            }

            @Override
            public boolean userExists(Long userId) {
                logFailure("userExists userId=" + userId, cause);
                throw mapToDomainException(cause);
            }
        };
    }

    private void logFailure(String method, Throwable cause) {
        log.error("Произошёл fallback UsersClient в методе {}. cause={}", method, cause.toString(), cause);
    }

    private RuntimeException mapToDomainException(Throwable cause) {
        if (cause instanceof FeignException.NotFound) {
            return new NotFoundException("User not found");
        }
        if (cause instanceof FeignException.Conflict) {
            return new NotFoundException("Conflig with mapping");
        }
        return new ServiceUnavailableException("users-service unavailable");
    }
}