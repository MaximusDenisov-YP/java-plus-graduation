package ru.yandex.practicum.event.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000, nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private Event event;

//    @ManyToOne
//    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
//    private User owner;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    private LocalDateTime created;
}
