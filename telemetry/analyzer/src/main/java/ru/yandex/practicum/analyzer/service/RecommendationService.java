package ru.yandex.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.analyzer.dao.InteractionDao;
import ru.yandex.practicum.analyzer.dao.SimilarityDao;
import ru.yandex.practicum.analyzer.model.EventSimilarity;
import ru.yandex.practicum.analyzer.model.RecommendedEvent;
import ru.yandex.practicum.analyzer.model.UserEventInteraction;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final InteractionDao interactionDao;
    private final SimilarityDao similarityDao;

    @Value("${recommendations.recent-interactions-limit}")
    private int recentInteractionsLimit;

    @Value("${recommendations.candidate-limit}")
    private int candidateLimit;

    @Value("${recommendations.nearest-neighbors-limit}")
    private int nearestNeighborsLimit;

    public List<RecommendedEvent> getInteractionsCount(List<Long> eventIds) {
        Map<Long, Double> counts = interactionDao.getInteractionsCount(eventIds);

        return counts.entrySet().stream()
                .map(entry -> new RecommendedEvent(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingDouble(RecommendedEvent::score).reversed())
                .toList();
    }

    public List<RecommendedEvent> getSimilarEvents(long eventId, long userId, int maxResults) {
        List<EventSimilarity> similarities = similarityDao.findSimilarEvents(eventId);
        Set<Long> interactedEventIds = interactionDao.findInteractedEventIds(userId);

        return similarities.stream()
                .map(similarity -> toRecommendedEvent(eventId, similarity))
                .filter(recommended -> recommended.eventId() != eventId)
                .filter(recommended -> !interactedEventIds.contains(recommended.eventId()))
                .sorted(Comparator.comparingDouble(RecommendedEvent::score).reversed())
                .limit(maxResults)
                .toList();
    }

    public List<RecommendedEvent> getRecommendationsForUser(long userId, int maxResults) {
        List<UserEventInteraction> recentInteractions =
                interactionDao.findRecentUserInteractions(userId, recentInteractionsLimit);

        if (recentInteractions.isEmpty()) {
            return List.of();
        }

        List<Long> sourceEventIds = recentInteractions.stream()
                .map(UserEventInteraction::eventId)
                .distinct()
                .toList();

        Set<Long> interactedEventIds = interactionDao.findInteractedEventIds(userId);

        List<Long> candidateEventIds = similarityDao.findSimilarEventsForEvents(sourceEventIds).stream()
                .map(similarity -> extractOtherEventId(sourceEventIds, similarity))
                .filter(Objects::nonNull)
                .filter(eventId -> !interactedEventIds.contains(eventId))
                .distinct()
                .limit(candidateLimit)
                .toList();

        if (candidateEventIds.isEmpty()) {
            return List.of();
        }

        Map<Long, Double> userWeights =
                interactionDao.getWeightsForUserAndEvents(userId, interactedEventIds);

        return candidateEventIds.stream()
                .map(candidateId -> predictScore(candidateId, sourceEventIds, userWeights))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(RecommendedEvent::score).reversed())
                .limit(maxResults)
                .toList();
    }

    private RecommendedEvent toRecommendedEvent(long sourceEventId, EventSimilarity similarity) {
        long otherEventId = similarity.eventA() == sourceEventId
                ? similarity.eventB()
                : similarity.eventA();

        return new RecommendedEvent(otherEventId, similarity.score());
    }

    private Long extractOtherEventId(Collection<Long> sourceEventIds, EventSimilarity similarity) {
        boolean containsA = sourceEventIds.contains(similarity.eventA());
        boolean containsB = sourceEventIds.contains(similarity.eventB());

        if (containsA && !containsB) {
            return similarity.eventB();
        }

        if (containsB && !containsA) {
            return similarity.eventA();
        }

        return null;
    }

    private RecommendedEvent predictScore(long candidateEventId,
                                          List<Long> viewedEventIds,
                                          Map<Long, Double> userWeights) {

        List<EventSimilarity> similarities =
                similarityDao.findSimilaritiesForTargetAndEvents(candidateEventId, viewedEventIds);

        List<EventSimilarity> nearestNeighbors = similarities.stream()
                .sorted(Comparator.comparingDouble(EventSimilarity::score).reversed())
                .limit(nearestNeighborsLimit)
                .toList();

        if (nearestNeighbors.isEmpty()) {
            return null;
        }

        double weightedSum = 0.0;
        double similaritySum = 0.0;

        for (EventSimilarity similarity : nearestNeighbors) {
            long neighborEventId = similarity.eventA() == candidateEventId
                    ? similarity.eventB()
                    : similarity.eventA();

            Double weight = userWeights.get(neighborEventId);
            if (weight == null) {
                continue;
            }

            weightedSum += similarity.score() * weight;
            similaritySum += similarity.score();
        }

        if (similaritySum == 0.0) {
            return null;
        }

        return new RecommendedEvent(candidateEventId, weightedSum / similaritySum);
    }
}