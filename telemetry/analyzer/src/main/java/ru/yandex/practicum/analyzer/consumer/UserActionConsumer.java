package ru.yandex.practicum.analyzer.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.dao.InteractionDao;
import ru.yandex.practicum.analyzer.service.ActionWeightResolver;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionConsumer {

    private final InteractionDao interactionDao;
    private final ActionWeightResolver actionWeightResolver;

    @KafkaListener(
            topics = "${analyzer.kafka.topic.user-actions}",
            containerFactory = "userActionKafkaListenerContainerFactory"
    )
    public void consume(UserActionAvro action) {
        double weight = actionWeightResolver.resolve(action.getActionType());

        log.info("Consuming user action: userId={}, eventId={}, actionType={}, weight={}",
                action.getUserId(),
                action.getEventId(),
                action.getActionType(),
                weight);

        interactionDao.upsertInteraction(
                action.getUserId(),
                action.getEventId(),
                weight,
                action.getTimestamp()
        );
    }
}