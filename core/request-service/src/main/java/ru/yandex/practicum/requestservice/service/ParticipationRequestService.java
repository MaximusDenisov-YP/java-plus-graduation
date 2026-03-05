package ru.yandex.practicum.requestservice.service;

import ru.yandex.practicum.contracts.dto.event.UpdateParticipationRequestListDto;
import ru.yandex.practicum.contracts.dto.request.ParticipationRequestDto;
import ru.yandex.practicum.contracts.dto.request.UpdateParticipationRequestDto;
import ru.yandex.practicum.requestservice.entity.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequest> getUserRequests(Long userId);

    ParticipationRequest addRequest(Long userId, Long eventId);

    ParticipationRequest cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getUserRequestsByEventId(Long userId, Long eventId);

    UpdateParticipationRequestListDto updateUserRequestsByEventId(Long userId, Long eventId, UpdateParticipationRequestDto updateParticipationRequestDto);
}
