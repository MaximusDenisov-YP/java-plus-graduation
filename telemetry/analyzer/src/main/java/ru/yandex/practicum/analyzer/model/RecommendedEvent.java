package ru.yandex.practicum.analyzer.model;

public record RecommendedEvent(
        long eventId,
        double score
) {
}