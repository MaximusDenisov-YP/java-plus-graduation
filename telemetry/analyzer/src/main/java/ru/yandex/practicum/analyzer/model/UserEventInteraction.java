package ru.yandex.practicum.analyzer.model;

import lombok.Builder;

import java.time.Instant;

@Builder
public record UserEventInteraction(
        long userId,
        long eventId,
        double weight,
        Instant updatedAt
) {
}
