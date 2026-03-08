package ru.yandex.practicum.analyzer.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.dao.SimilarityDao;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSimilarityConsumer {

    private final SimilarityDao similarityDao;

    @KafkaListener(
            topics = "${analyzer.kafka.topic.event-similarity}",
            containerFactory = "eventSimilarityKafkaListenerContainerFactory"
    )
    public void consume(EventSimilarityAvro similarity) {
        log.info("Consuming similarity: eventA={}, eventB={}, score={}",
                similarity.getEventA(),
                similarity.getEventB(),
                similarity.getScore());

        similarityDao.upsertSimilarity(
                similarity.getEventA(),
                similarity.getEventB(),
                similarity.getScore(),
                similarity.getTimestamp()
        );
    }
}