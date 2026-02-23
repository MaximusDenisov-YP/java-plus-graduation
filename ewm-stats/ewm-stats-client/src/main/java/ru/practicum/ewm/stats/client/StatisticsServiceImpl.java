package ru.practicum.ewm.stats.client;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stats.dto.CreateHitDto;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.ewm.stats.dto.util.DateFormatter.format;

@Slf4j
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final RestTemplate restTemplate;
    private final String statsServerUrl;

    @Override
    public List<ViewStats> getStats(String start, String end, List<String> uris) {
        return List.of();
    }

    @Override
    public void sendStats(List<Long> eventIds, HttpServletRequest request) {
        CreateHitDto hit = CreateHitDto.builder()
                .app("ewm-main")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        String url = statsServerUrl + "/hit";
        restTemplate.postForEntity(url, hit, Void.class);
    }

    @Override
    public Map<Long, Long> getEventsViews(List<Long> eventIds, HttpServletRequest request, boolean sendHit) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptyMap();
        }

        if (sendHit && request != null) {
            sendStats(eventIds, request);
        }

        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        LocalDateTime start = LocalDateTime.now().minusYears(1);
        LocalDateTime end = LocalDateTime.now();

        List<ViewStats> stats = getStats(format(start), format(end), uris);

        Map<Long, Long> viewsMap = new HashMap<>();
        eventIds.forEach(id -> viewsMap.put(id, 0L));

        stats.forEach(stat -> {
            Long eventId = extractEventIdFromUri(stat.getUri());
            if (eventId != null && viewsMap.containsKey(eventId)) {
                viewsMap.put(eventId, stat.getHits());
            }
        });
        return viewsMap;
    }

    private Long extractEventIdFromUri(String uri) {
        if (uri == null || !uri.startsWith("/events/")) {
            return null;
        }
        try {
            String idStr = uri.substring("/events/".length());
            return Long.parseLong(idStr);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return null;
        }
    }
}
