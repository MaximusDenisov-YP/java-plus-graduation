package ru.yandex.practicum.analyzer.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.ewm.stats.avro.ActionTypeAvro;

@Component
public class ActionWeightResolver {

    public double resolve(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> 1.0;
            case REGISTER -> 2.0;
            case LIKE -> 3.0;
        };
    }
}