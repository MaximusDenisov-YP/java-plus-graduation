package ru.yandex.practicum.analyzer.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.analyzer.model.RecommendedEvent;
import ru.yandex.practicum.analyzer.service.RecommendationService;
import ru.yandex.practicum.stats.service.dashboard.InteractionsCountRequestProto;
import ru.yandex.practicum.stats.service.dashboard.RecommendationsControllerGrpc;
import ru.yandex.practicum.stats.service.dashboard.RecommendedEventProto;
import ru.yandex.practicum.stats.service.dashboard.SimilarEventsRequestProto;
import ru.yandex.practicum.stats.service.dashboard.UserPredictionsRequestProto;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class RecommendationsGrpcController
        extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final RecommendationService recommendationService;

    @Override
    public void getInteractionsCount(
            InteractionsCountRequestProto request,
            StreamObserver<RecommendedEventProto> responseObserver
    ) {
        List<RecommendedEvent> events =
                recommendationService.getInteractionsCount(request.getEventIdList());

        events.stream()
                .map(this::toProto)
                .forEach(responseObserver::onNext);

        responseObserver.onCompleted();
    }

    @Override
    public void getSimilarEvents(
            SimilarEventsRequestProto request,
            StreamObserver<RecommendedEventProto> responseObserver
    ) {
        List<RecommendedEvent> events = recommendationService.getSimilarEvents(
                request.getEventId(),
                request.getUserId(),
                request.getMaxResults()
        );

        events.stream()
                .map(this::toProto)
                .forEach(responseObserver::onNext);

        responseObserver.onCompleted();
    }

    @Override
    public void getRecommendationsForUser(
            UserPredictionsRequestProto request,
            StreamObserver<RecommendedEventProto> responseObserver
    ) {
        List<RecommendedEvent> events = recommendationService.getRecommendationsForUser(
                request.getUserId(),
                request.getMaxResults()
        );

        events.stream()
                .map(this::toProto)
                .forEach(responseObserver::onNext);

        responseObserver.onCompleted();
    }

    private RecommendedEventProto toProto(RecommendedEvent event) {
        return RecommendedEventProto.newBuilder()
                .setEventId(event.eventId())
                .setScore(event.score())
                .build();
    }
}