package ru.yandex.practicum.aggregator.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.service.AggregationService;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionConsumer {

    private final AggregationService aggregationService;

    @KafkaListener(
            topics = "${aggregator.kafka.topic.user-actions}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(UserActionAvro action) {
        log.info("Received action from Kafka: userId={}, eventId={}, actionType={}",
                action.getUserId(),
                action.getEventId(),
                action.getActionType());

        aggregationService.process(action);
    }
}