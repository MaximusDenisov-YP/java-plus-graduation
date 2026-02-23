package ru.practicum.ewm.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main.entity.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByInitiator_Id(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    List<Event> findByIdIn(List<Long> eventIds);

    Boolean existsByCategoryId(Long categoryId);

    Optional<Event> findByIdAndPublishedOnIsNotNull(Long id);

    @Query("""
                SELECT e FROM Event e
                LEFT JOIN FETCH e.comments c
                GROUP BY e
                ORDER BY COUNT(c) DESC
                LIMIT :count
            """)
    List<Event> getTopByComments(@Param("count") int count);

}