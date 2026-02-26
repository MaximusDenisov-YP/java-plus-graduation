package ru.yandex.practicum.contracts.feign.requests;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.contracts.dto.event.UpdateParticipationRequestListDto;
import ru.yandex.practicum.contracts.dto.request.ParticipationRequestDto;
import ru.yandex.practicum.contracts.dto.request.UpdateParticipationRequestDto;

import java.util.List;

@FeignClient(name = "requests-service")
public interface RequestsClient {

    @GetMapping("/users/{userId}/event/{eventId}")
    List<ParticipationRequestDto> getUserRequestsByEventId(
            @PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId);

    @PutMapping("/users/{userId}/event/{eventId}")
    UpdateParticipationRequestListDto updateUserRequestsByEventId(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @RequestBody UpdateParticipationRequestDto updateDto);
}
