package ru.yandex.practicum.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.stats.avro.EventSimilarityAvro;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimilarityProducer {

    private final KafkaTemplate<String, EventSimilarityAvro> kafkaTemplate;

    @Value("${aggregator.kafka.topic.event-similarity}")
    private String topic;

    public void send(EventSimilarityAvro similarity) {
        String key = similarity.getEventA() + "-" + similarity.getEventB();

        kafkaTemplate.send(topic, key, similarity)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send similarity: {}-{}",
                                similarity.getEventA(),
                                similarity.getEventB(),
                                ex);
                    } else {
                        log.info("Similarity sent: pair=({},{}) score={}",
                                similarity.getEventA(),
                                similarity.getEventB(),
                                similarity.getScore());
                    }
                });
    }
}