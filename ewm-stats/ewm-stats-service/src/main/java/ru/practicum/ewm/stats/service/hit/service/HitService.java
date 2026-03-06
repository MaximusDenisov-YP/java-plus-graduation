package ru.yandex.practicum.ewm.stats.service.hit.service;

import ru.yandex.practicum.ewm.stats.dto.GetStatsDto;
import ru.yandex.practicum.ewm.stats.dto.HitDto;
import ru.yandex.practicum.ewm.stats.dto.CreateHitDto;
import ru.yandex.practicum.ewm.stats.dto.ViewStats;

import java.util.List;

public interface HitService {
    HitDto create(CreateHitDto hit);

    List<ViewStats> getStats(GetStatsDto getStatsDto);

}
