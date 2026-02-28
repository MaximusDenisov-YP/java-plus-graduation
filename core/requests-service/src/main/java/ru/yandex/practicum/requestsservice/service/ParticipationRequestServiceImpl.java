package ru.yandex.practicum.requestsservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.contracts.dto.event.EventFullDto;
import ru.yandex.practicum.contracts.dto.event.UpdateEventAdminDto;
import ru.yandex.practicum.contracts.dto.event.UpdateParticipationRequestListDto;
import ru.yandex.practicum.contracts.dto.request.ParticipationRequestDto;
import ru.yandex.practicum.contracts.dto.request.UpdateParticipationRequestDto;
import ru.yandex.practicum.contracts.enums.EventState;
import ru.yandex.practicum.contracts.enums.RequestStatus;
import ru.yandex.practicum.contracts.exception.ConflictException;
import ru.yandex.practicum.contracts.exception.ForbiddenException;
import ru.yandex.practicum.contracts.exception.NotFoundException;
import ru.yandex.practicum.contracts.feign.events.EventsClient;
import ru.yandex.practicum.contracts.feign.users.UsersClient;
import ru.yandex.practicum.requestsservice.entity.ParticipationRequest;
import ru.yandex.practicum.requestsservice.mapper.ParticipationRequestMapper;
import ru.yandex.practicum.requestsservice.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final UsersClient usersClient;
    private final EventsClient eventsClient;
    private final ParticipationRequestRepository requestRepository;
    private final ParticipationRequestMapper participationRequestMapper;

    @Transactional
    public List<ParticipationRequest> getUserRequests(Long userId) {
        if (!usersClient.userExists(userId)) {
            throw new NotFoundException("User with id=%d was not found".formatted(userId));
        }
        return requestRepository.findByRequesterId(userId);
    }

    @Transactional
    public ParticipationRequest addRequest(Long userId, Long eventId) {
        if (!usersClient.userExists(userId)) {
            throw new NotFoundException("User with id=%d was not found".formatted(userId));
        }

        EventFullDto event;
        try {
            event = eventsClient.getEventById(eventId);
        } catch (FeignException.NotFound ex) {
            throw new NotFoundException("Event with id=%d was not found\"".formatted(eventId));
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot request own event");
        }

        if (!event.getState().equals(EventState.PUBLISHED.toString())) {
            throw new ConflictException("Event is not published");
        }

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Request already exists");
        }

        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Participant limit reached");
        }

        ParticipationRequest request = new ParticipationRequest();
        request.setRequesterId(userId);
        request.setEventId(eventId);
        request.setCreated(LocalDateTime.now());
        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventsClient.updateAdminEvent(
                    event.getId(),
                    UpdateEventAdminDto.builder()
                            .confirmedRequests(event.getConfirmedRequests())
                            .build()
            );
        }

        return requestRepository.save(request);
    }

    @Transactional
    public ParticipationRequest cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=%d was not found".formatted(requestId)));

        if (!request.getRequesterId().equals(userId)) {
            throw new ForbiddenException("Cannot cancel another user's request");
        }

        request.setStatus(RequestStatus.CANCELED);
        return requestRepository.save(request);
    }

    @Override
    public List<ParticipationRequestDto> getUserRequestsByEventId(Long userId, Long eventId) {

        EventFullDto event;
        try {
            event = eventsClient.getEventById(eventId);
        } catch (FeignException.NotFound ex) {
            throw new NotFoundException("Event with id=%d was not found\"".formatted(eventId));
        }

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("User is not the initiator of the event");
        }

        List<ParticipationRequest> requests = requestRepository.findByEventId(eventId);

        return requests.stream()
                .map(participationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UpdateParticipationRequestListDto updateUserRequestsByEventId(Long userId, Long eventId, UpdateParticipationRequestDto updateDto) {

        EventFullDto event;
        try {
            event = eventsClient.getEventById(eventId);
        } catch (FeignException.NotFound ex) {
            throw new NotFoundException("Event with id=%d was not found\"".formatted(eventId));
        }

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("User is not the initiator of the event");
        }

        List<Long> requestIds = updateDto.getRequestsId();
        if (requestIds == null || requestIds.isEmpty()) {
            List<ParticipationRequest> pendingRequests = requestRepository.findByEventIdAndStatus(eventId, RequestStatus.PENDING);
            if (pendingRequests.isEmpty()) {
                throw new ConflictException("No pending requests found for this event");
            }
            requestIds = pendingRequests.stream()
                    .map(ParticipationRequest::getId)
                    .collect(Collectors.toList());
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(requestIds);

        if (requests.size() != requestIds.size()) {
            throw new NotFoundException("Some request IDs were not found");
        }

        for (ParticipationRequest request : requests) {
            if (!request.getEventId().equals(eventId)) {
                throw new ForbiddenException("Request with id=" + request.getId() + " does not belong to this event");
            }

            if (updateDto.getStatus() == RequestStatus.CONFIRMED && request.getStatus() == RequestStatus.CONFIRMED) {
                throw new ConflictException("Request is already confirmed");
            }
        }

        RequestStatus newStatus = updateDto.getStatus();

        if (newStatus == RequestStatus.CONFIRMED) {
            long currentConfirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            long limit = event.getParticipantLimit() == null ? 0 : event.getParticipantLimit();
            if (limit == 0) {
                requests.forEach(r -> r.setStatus(RequestStatus.CONFIRMED));
            } else {
                long availableSlots = limit - currentConfirmed;

                if (availableSlots <= 0) {
                    requests.forEach(r -> r.setStatus(RequestStatus.REJECTED));
                } else {
                    List<ParticipationRequest> toConfirm = requests.stream()
                            .filter(r -> r.getStatus() != RequestStatus.CONFIRMED)
                            .limit(availableSlots)
                            .collect(Collectors.toList());

                    List<ParticipationRequest> toReject = requests.stream()
                            .filter(r -> !toConfirm.contains(r))
                            .collect(Collectors.toList());

                    toConfirm.forEach(r -> r.setStatus(RequestStatus.CONFIRMED));
                    toReject.forEach(r -> r.setStatus(RequestStatus.REJECTED));
                }
            }
        } else {
            requests.forEach(r -> r.setStatus(newStatus));
        }
        List<ParticipationRequest> updatedRequests = requestRepository.saveAll(requests);
        updateEventConfirmedRequests(event);
        UpdateParticipationRequestListDto result = new UpdateParticipationRequestListDto();

        for (ParticipationRequest request : updatedRequests) {
            if (request.getStatus() == RequestStatus.CONFIRMED) {
                result.getConfirmedRequests().add(participationRequestMapper.toDto(request));
            } else if (request.getStatus() == RequestStatus.REJECTED) {
                result.getRejectedRequests().add(participationRequestMapper.toDto(request));
            }
        }

        return result;
    }

    private void updateEventConfirmedRequests(EventFullDto eventFullDto) {
        Long confirmedCount = requestRepository.countByEventIdAndStatus(eventFullDto.getId(), RequestStatus.CONFIRMED);
        eventFullDto.setConfirmedRequests(confirmedCount);

        UpdateEventAdminDto updateEvent = UpdateEventAdminDto.builder()
                .annotation(eventFullDto.getAnnotation())
                .category(eventFullDto.getCategory().getId())
                .description(eventFullDto.getDescription())
                .eventDate(eventFullDto.getEventDate())
                .location(eventFullDto.getLocation())
                .paid(eventFullDto.getPaid())
                .confirmedRequests(eventFullDto.getConfirmedRequests())
                .participantLimit(eventFullDto.getParticipantLimit())
                .requestModeration(eventFullDto.getRequestModeration())
                .title(eventFullDto.getTitle())
                .build();

        eventsClient.updateAdminEvent(eventFullDto.getId(), updateEvent);
        log.info("Updated confirmedRequests to {} for event {}", confirmedCount, eventFullDto.getId());
    }
}