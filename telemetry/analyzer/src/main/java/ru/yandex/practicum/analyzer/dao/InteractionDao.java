package ru.yandex.practicum.analyzer.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.analyzer.model.UserEventInteraction;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class InteractionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void upsertInteraction(long userId, long eventId, double weight, Instant updatedAt) {
        String sql = """
                insert into user_event_interactions (user_id, event_id, weight, updated_at)
                values (:userId, :eventId, :weight, :updatedAt)
                on conflict (user_id, event_id)
                do update set
                    weight = greatest(user_event_interactions.weight, excluded.weight),
                    updated_at = excluded.updated_at
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("eventId", eventId)
                .addValue("weight", weight)
                .addValue("updatedAt", Timestamp.from(updatedAt));

        jdbcTemplate.update(sql, params);
    }

    public List<UserEventInteraction> findRecentUserInteractions(long userId, int limit) {
        String sql = """
                select user_id, event_id, weight, updated_at
                from user_event_interactions
                where user_id = :userId
                order by updated_at desc
                limit :limit
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("limit", limit);

        return jdbcTemplate.query(sql, params, (rs, rowNum) ->
                UserEventInteraction.builder()
                        .userId(rs.getLong("user_id"))
                        .eventId(rs.getLong("event_id"))
                        .weight(rs.getDouble("weight"))
                        .updatedAt(rs.getTimestamp("updated_at").toInstant())
                        .build()
        );
    }

    public Set<Long> findInteractedEventIds(long userId) {
        String sql = """
                select event_id
                from user_event_interactions
                where user_id = :userId
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId);

        return Set.copyOf(jdbcTemplate.query(
                sql,
                params,
                (rs, rowNum) -> rs.getLong("event_id")
        ));
    }

    public Map<Long, Double> getWeightsForUserAndEvents(long userId, Collection<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Map.of();
        }

        String sql = """
                select event_id, weight
                from user_event_interactions
                where user_id = :userId
                  and event_id in (:eventIds)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("eventIds", eventIds);

        return jdbcTemplate.query(sql, params, rs -> {
            Map<Long, Double> result = new java.util.HashMap<>();
            while (rs.next()) {
                result.put(rs.getLong("event_id"), rs.getDouble("weight"));
            }
            return result;
        });
    }

    public Map<Long, Double> getInteractionsCount(Collection<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Map.of();
        }

        String sql = """
                select event_id, sum(weight) as score
                from user_event_interactions
                where event_id in (:eventIds)
                group by event_id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("eventIds", eventIds);

        return jdbcTemplate.query(sql, params, rs -> {
            Map<Long, Double> result = new java.util.HashMap<>();
            while (rs.next()) {
                result.put(rs.getLong("event_id"), rs.getDouble("score"));
            }
            return result;
        });
    }
}