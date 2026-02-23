package ru.practicum.ewm.main.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.main.enums.EventState;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventSearchRequest {
    private List<Long> users;
    private List<EventState> states;
    private List<Long> categories;
    private String rangeStart;
    private String rangeEnd;
    private Integer from;
    private Integer size;

    private static final Integer DEFAULT_FROM = 0;
    private static final Integer DEFAULT_SIZE = 10;

    public static AdminEventSearchRequest fromParams(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size) {

        return AdminEventSearchRequest.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from != null ? from : DEFAULT_FROM)
                .size(size != null ? size : DEFAULT_SIZE)
                .build();
    }
}
