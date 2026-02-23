package ru.practicum.ewm.main.service.request;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dto.event.UpdateParticipationRequestListDto;
import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.main.dto.request.UpdateParticipationRequestDto;
import ru.practicum.ewm.main.entity.Event;
import ru.practicum.ewm.main.entity.ParticipationRequest;
import ru.practicum.ewm.main.entity.User;
import ru.practicum.ewm.main.enums.EventState;
import ru.practicum.ewm.main.enums.RequestStatus;
import ru.practicum.ewm.main.exception.*;
import ru.practicum.ewm.main.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.ParticipationRequestRepository;
import ru.practicum.ewm.main.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository requestRepository;
    private final ParticipationRequestMapper participationRequestMapper;

    @Transactional
    public List<ParticipationRequest> getUserRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=%d was not found".formatted(userId)));
        return requestRepository.findByRequesterId(userId);
    }

    @Transactional
    public ParticipationRequest addRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=%d was not found".formatted(userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=%d was not found".formatted(eventId)));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot request own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event is not published");
        }

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Request already exists");
        }

        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Participant limit reached");
        }

        ParticipationRequest request = new ParticipationRequest();
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }

        return requestRepository.save(request);
    }

    @Transactional
    public ParticipationRequest cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=%d was not found".formatted(requestId)));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ForbiddenException("Cannot cancel another user's request");
        }

        request.setStatus(RequestStatus.CANCELED);
        return requestRepository.save(request);
    }

    @Override
    public List<ParticipationRequestDto> getUserRequestsByEventId(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

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
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

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
            if (!request.getEvent().getId().equals(eventId)) {
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

    private void updateEventConfirmedRequests(Event event) {
        Long confirmedCount = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        event.setConfirmedRequests(confirmedCount);
        eventRepository.save(event);
        log.info("Updated confirmedRequests to {} for event {}", confirmedCount, event.getId());
    }
}