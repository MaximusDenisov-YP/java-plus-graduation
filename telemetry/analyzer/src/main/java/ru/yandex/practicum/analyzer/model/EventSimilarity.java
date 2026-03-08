package ru.yandex.practicum.analyzer.model;

import lombok.Builder;

import java.time.Instant;

@Builder
public record EventSimilarity(
        long eventA,
        long eventB,
        double score,
        Instant updatedAt
) {
}