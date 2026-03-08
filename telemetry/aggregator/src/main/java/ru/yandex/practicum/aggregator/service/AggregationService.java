package ru.yandex.practicum.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.aggregator.model.EventPair;
import ru.yandex.practicum.aggregator.util.ActionWeightResolver;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationService {

    private final ActionWeightResolver weightResolver;
    private final SimilarityProducer similarityProducer;

    private final Map<Long, Map<Long, Double>> userEventWeights = new HashMap<>();
    private final Map<Long, Double> eventWeightSums = new HashMap<>();
    private final Map<EventPair, Double> pairMinSums = new HashMap<>();

    public void process(UserActionAvro action) {
        long userId = action.getUserId();
        long eventId = action.getEventId();

        double newWeightCandidate = weightResolver.resolve(action.getActionType());

        Map<Long, Double> userWeights =
                userEventWeights.computeIfAbsent(userId, k -> new HashMap<>());

        double oldWeight = userWeights.getOrDefault(eventId, 0.0);
        double newWeight = Math.max(oldWeight, newWeightCandidate);

        if (Double.compare(newWeight, oldWeight) == 0) {
            log.debug("Skipping action because max weight did not change. userId={}, eventId={}", userId, eventId);
            return;
        }

        userWeights.put(eventId, newWeight);
        eventWeightSums.merge(eventId, newWeight - oldWeight, Double::sum);

        for (Map.Entry<Long, Double> entry : userWeights.entrySet()) {
            long otherEventId = entry.getKey();
            double otherWeight = entry.getValue();

            if (otherEventId == eventId) {
                continue;
            }

            EventPair pair = EventPair.of(eventId, otherEventId);

            double oldMin = Math.min(oldWeight, otherWeight);
            double newMin = Math.min(newWeight, otherWeight);
            double delta = newMin - oldMin;

            pairMinSums.merge(pair, delta, Double::sum);

            double sumA = eventWeightSums.getOrDefault(pair.eventA(), 0.0);
            double sumB = eventWeightSums.getOrDefault(pair.eventB(), 0.0);
            double minSum = pairMinSums.getOrDefault(pair, 0.0);

            if (sumA == 0.0 || sumB == 0.0) {
                continue;
            }

            double similarity = minSum / (sumA * sumB);

            EventSimilarityAvro similarityAvro = EventSimilarityAvro.newBuilder()
                    .setEventA(pair.eventA())
                    .setEventB(pair.eventB())
                    .setScore(similarity)
                    .setTimestamp(action.getTimestamp())
                    .build();

            similarityProducer.send(similarityAvro);
        }
    }
}