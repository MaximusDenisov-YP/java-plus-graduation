package ru.yandex.practicum.requestservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.contracts.enums.RequestStatus;
import ru.yandex.practicum.requestservice.entity.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByRequesterId(Long userId);

    Boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<ParticipationRequest> findAllByRequesterIdAndEventId(Long userId, Long eventId);

    List<ParticipationRequest> findByEventIdAndStatus(Long eventId, RequestStatus status);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findByEventId(Long eventId);
}