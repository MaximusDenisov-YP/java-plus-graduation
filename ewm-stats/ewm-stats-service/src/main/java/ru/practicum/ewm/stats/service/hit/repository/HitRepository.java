package ru.practicum.ewm.stats.service.hit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.service.hit.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query(value = """
        SELECT new ru.practicum.ewm.stats.dto.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip))
        FROM Hit h
        WHERE h.timestamp BETWEEN :start AND :end
          AND (:uris IS NULL OR h.uri IN :uris)
        GROUP BY h.app, h.uri
        ORDER BY COUNT(DISTINCT h.ip) DESC
        """)
    List<ViewStats> getViewStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

    @Query(value = """
        SELECT new ru.practicum.ewm.stats.dto.ViewStats(h.app, h.uri, COUNT(h))
        FROM Hit h
        WHERE h.timestamp BETWEEN :start AND :end
          AND (:uris IS NULL OR h.uri IN :uris)
        GROUP BY h.app, h.uri
        ORDER BY COUNT(h) DESC
        """)
    List<ViewStats> getViewStatsNonUnique(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

}
