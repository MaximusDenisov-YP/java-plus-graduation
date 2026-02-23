package ru.practicum.ewm.main.controller.priv;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.main.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.main.service.request.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class PrivateRequestController {

    private final ParticipationRequestService service;
    private final ParticipationRequestMapper participationRequestMapper;

    @GetMapping
    public List<ParticipationRequestDto> getRequests(@PathVariable("userId") @NotNull @Positive Long userId) {
        return service.getUserRequests(userId).stream()
                .map(participationRequestMapper::toDto)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable("userId") @NotNull @Positive Long userId,
                                              @RequestParam("eventId") @NotNull @Positive Long eventId) {
        return participationRequestMapper.toDto(service.addRequest(userId, eventId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable("userId") @NotNull @Positive Long userId,
                                                 @PathVariable("requestId") @NotNull @Positive Long requestId) {
        return participationRequestMapper.toDto(service.cancelRequest(userId, requestId));
    }
}