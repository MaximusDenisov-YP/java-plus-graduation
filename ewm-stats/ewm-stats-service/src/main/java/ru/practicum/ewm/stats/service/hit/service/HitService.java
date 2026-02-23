package ru.practicum.ewm.stats.service.hit.service;

import ru.practicum.ewm.stats.dto.GetStatsDto;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.dto.CreateHitDto;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.util.List;

public interface HitService {
    HitDto create(CreateHitDto hit);

    List<ViewStats> getStats(GetStatsDto getStatsDto);

}
