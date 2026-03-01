package ru.yandex.practicum.requestservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contracts.dto.event.UpdateParticipationRequestListDto;
import ru.yandex.practicum.contracts.dto.request.ParticipationRequestDto;
import ru.yandex.practicum.contracts.dto.request.UpdateParticipationRequestDto;
import ru.yandex.practicum.requestservice.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalRequestController {

    private final ParticipationRequestService requestService;

    @GetMapping("/users/{userId}/event/{eventId}")
    public List<ParticipationRequestDto> getUserRequestsByEventId(
            @PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId) {
        return requestService.getUserRequestsByEventId(userId, eventId);
    }

    @PutMapping("/users/{userId}/event/{eventId}")
    public UpdateParticipationRequestListDto updateUserRequestsByEventId(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @RequestBody UpdateParticipationRequestDto updateDto) {
        return requestService.updateUserRequestsByEventId(userId, eventId, updateDto);
    }
}
