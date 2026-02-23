package ru.practicum.ewm.main.service.request;

import ru.practicum.ewm.main.dto.event.UpdateParticipationRequestListDto;
import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.main.dto.request.UpdateParticipationRequestDto;
import ru.practicum.ewm.main.entity.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequest> getUserRequests(Long userId);

    ParticipationRequest addRequest(Long userId, Long eventId);

    ParticipationRequest cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getUserRequestsByEventId(Long userId, Long eventId);

    UpdateParticipationRequestListDto updateUserRequestsByEventId(Long userId, Long eventId, UpdateParticipationRequestDto updateParticipationRequestDto);
}
