package ru.practicum.ewm.stats.client;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

    List<ViewStats> getStats(String start, String end, List<String> uris);

    void sendStats(List<Long> eventIds, HttpServletRequest request);

    Map<Long, Long> getEventsViews(List<Long> eventIds, HttpServletRequest request, boolean sendHit);

}
