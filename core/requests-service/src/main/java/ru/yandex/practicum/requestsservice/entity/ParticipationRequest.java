package ru.yandex.practicum.requestsservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.contracts.enums.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "participation_requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "event_id")
//    private Event event;

    private Long eventId;

//    @ManyToOne
//    @JoinColumn(name = "requester_id")
//    private User requester;

    private Long requesterId;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private LocalDateTime created;
}