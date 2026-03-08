package ru.yandex.practicum.statsclient.client.collector;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.stats.service.collector.ActionTypeProto;
import ru.yandex.practicum.stats.service.collector.UserActionControllerGrpc;
import ru.yandex.practicum.stats.service.collector.UserActionProto;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CollectorClient {

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub client;

    public void sendAction(long userId, long eventId, ActionTypeProto actionType) {

        Instant now = Instant.now();

        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();

        UserActionProto request = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(actionType)
                .setTimestamp(timestamp)
                .build();

        client.collectUserAction(request);
    }
}