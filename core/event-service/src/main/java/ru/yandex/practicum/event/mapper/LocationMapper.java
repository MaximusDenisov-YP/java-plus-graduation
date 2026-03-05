package ru.yandex.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.contracts.dto.event.LocationDto;
import ru.yandex.practicum.event.entity.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDto toDto(Location location);

    @Mapping(target = "id", source = "id")
    Location toEntity(LocationDto locationDto);
}