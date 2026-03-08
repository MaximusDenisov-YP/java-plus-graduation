package ru.yandex.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionProducer {

    private final KafkaTemplate<String, UserActionAvro> userActionKafkaTemplate;

    @Value("${collector.kafka.topic.user-actions}")
    private String topic;

    public void send(UserActionAvro action) {
        String key = String.valueOf(action.getEventId());

        userActionKafkaTemplate.send(topic, key, action)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send user action to Kafka. userId={}, eventId={}, actionType={}",
                                action.getUserId(),
                                action.getEventId(),
                                action.getActionType(),
                                ex);
                    } else {
                        log.info("User action sent to Kafka. topic={}, partition={}, offset={}, userId={}, eventId={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                action.getUserId(),
                                action.getEventId());
                    }
                });
    }
}