package ru.yandex.practicum.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.collector.mapper.UserActionMapper;
import ru.yandex.practicum.collector.service.UserActionProducer;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.stats.service.collector.UserActionControllerGrpc;
import ru.yandex.practicum.stats.service.collector.UserActionProto;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserActionGrpcController extends UserActionControllerGrpc.UserActionControllerImplBase {

    private final UserActionMapper userActionMapper;
    private final UserActionProducer userActionProducer;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received user action via gRPC: userId={}, eventId={}, actionType={}",
                    request.getUserId(),
                    request.getEventId(),
                    request.getActionType());

            UserActionAvro avro = userActionMapper.toAvro(request);
            userActionProducer.send(avro);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Failed to process user action. userId={}, eventId={}",
                    request.getUserId(),
                    request.getEventId(),
                    e);
            responseObserver.onError(e);
        }
    }
}