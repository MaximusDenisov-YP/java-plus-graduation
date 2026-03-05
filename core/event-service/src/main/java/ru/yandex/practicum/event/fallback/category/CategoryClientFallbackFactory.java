package ru.yandex.practicum.event.fallback.category;

import feign.FeignException;
import ru.yandex.practicum.contracts.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.contracts.dto.category.CategoryDto;
import ru.yandex.practicum.contracts.exception.NotFoundException;

import java.util.List;

@Component
@Slf4j
public class CategoryClientFallbackFactory implements FallbackFactory<CategoryClientWithFallback> {

    @Override
    public CategoryClientWithFallback create(Throwable cause) {
        return new CategoryClientWithFallback() {

            @Override
            public CategoryDto getCategory(Long categoryId) {
                logFailure("getCategory categoryId=" + categoryId, cause);

                if (cause instanceof FeignException.NotFound) {
                    throw new NotFoundException("Category not found: " + categoryId);
                }
                throw new ServiceUnavailableException("extra-service unavailable");
            }

            @Override
            public List<CategoryDto> getCategoriesByIds(List<Long> categoriesIds) {
                logFailure("getCategoriesByIds ids=" + categoriesIds, cause);
                throw new ServiceUnavailableException("extra-service unavailable");
            }

            @Override
            public boolean categoryExists(Long categoryId) {
                logFailure("categoryExists categoryId=" + categoryId, cause);

                if (cause instanceof FeignException.NotFound) {
                    return false;
                }
                throw new ServiceUnavailableException("extra-service unavailable");
            }
        };
    }

    private void logFailure(String method, Throwable cause) {
        log.error("Произошёл fallback CategoryClient в методе {}. cause={}", method, cause.toString(), cause);
    }
}