package ru.yandex.practicum.aggregator.model;

public record EventPair(long eventA, long eventB) {

    public static EventPair of(long first, long second) {
        return first < second
                ? new EventPair(first, second)
                : new EventPair(second, first);
    }
}