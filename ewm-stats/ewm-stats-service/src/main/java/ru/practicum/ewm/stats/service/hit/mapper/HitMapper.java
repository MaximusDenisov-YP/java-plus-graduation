package ru.yandex.practicum.ewm.stats.service.hit.mapper;

import ru.yandex.practicum.ewm.stats.dto.CreateHitDto;
import ru.yandex.practicum.ewm.stats.dto.HitDto;
import ru.yandex.practicum.ewm.stats.service.hit.model.Hit;

public class HitMapper {
    public static Hit toHit(CreateHitDto endPointHitCreateDto) {
        return Hit.builder()
                .app(endPointHitCreateDto.getApp())
                .uri(endPointHitCreateDto.getUri())
                .ip(endPointHitCreateDto.getIp())
                .timestamp(endPointHitCreateDto.getTimestamp())
                .build();
    }

    public static HitDto toEndPointHitDto(Hit hit) {
        return HitDto.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp())
                .build();
    }

}
