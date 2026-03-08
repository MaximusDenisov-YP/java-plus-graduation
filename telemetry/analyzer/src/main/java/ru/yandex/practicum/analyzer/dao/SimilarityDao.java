package ru.yandex.practicum.analyzer.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.analyzer.model.EventSimilarity;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SimilarityDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void upsertSimilarity(long eventA, long eventB, double score, Instant updatedAt) {
        String sql = """
                insert into event_similarity (event_a, event_b, score, updated_at)
                values (:eventA, :eventB, :score, :updatedAt)
                on conflict (event_a, event_b)
                do update set
                    score = excluded.score,
                    updated_at = excluded.updated_at
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("eventA", eventA)
                .addValue("eventB", eventB)
                .addValue("score", score)
                .addValue("updatedAt", Timestamp.from(updatedAt));

        jdbcTemplate.update(sql, params);
    }

    public List<EventSimilarity> findSimilarEvents(long eventId) {
        String sql = """
                select event_a, event_b, score, updated_at
                from event_similarity
                where event_a = :eventId or event_b = :eventId
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("eventId", eventId);

        return jdbcTemplate.query(sql, params, (rs, rowNum) ->
                EventSimilarity.builder()
                        .eventA(rs.getLong("event_a"))
                        .eventB(rs.getLong("event_b"))
                        .score(rs.getDouble("score"))
                        .updatedAt(rs.getTimestamp("updated_at").toInstant())
                        .build()
        );
    }

    public List<EventSimilarity> findSimilarEventsForEvents(Collection<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return List.of();
        }

        String sql = """
                select event_a, event_b, score, updated_at
                from event_similarity
                where event_a in (:eventIds) or event_b in (:eventIds)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("eventIds", eventIds);

        return jdbcTemplate.query(sql, params, (rs, rowNum) ->
                EventSimilarity.builder()
                        .eventA(rs.getLong("event_a"))
                        .eventB(rs.getLong("event_b"))
                        .score(rs.getDouble("score"))
                        .updatedAt(rs.getTimestamp("updated_at").toInstant())
                        .build()
        );
    }

    public List<EventSimilarity> findSimilaritiesForTargetAndEvents(long targetEventId, Collection<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return List.of();
        }

        String sql = """
                select event_a, event_b, score, updated_at
                from event_similarity
                where (event_a = :targetEventId and event_b in (:eventIds))
                   or (event_b = :targetEventId and event_a in (:eventIds))
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("targetEventId", targetEventId)
                .addValue("eventIds", eventIds);

        return jdbcTemplate.query(sql, params, (rs, rowNum) ->
                EventSimilarity.builder()
                        .eventA(rs.getLong("event_a"))
                        .eventB(rs.getLong("event_b"))
                        .score(rs.getDouble("score"))
                        .updatedAt(rs.getTimestamp("updated_at").toInstant())
                        .build()
        );
    }
}