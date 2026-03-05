package ru.yandex.practicum.requestservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.contracts.dto.request.ParticipationRequestDto;
import ru.yandex.practicum.contracts.util.DateFormatter;
import ru.yandex.practicum.requestservice.entity.ParticipationRequest;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {

    @Mapping(target = "created", expression = "java(formatDate(request.getCreated()))")
    @Mapping(target = "requester", source = "requesterId")
    @Mapping(target = "event", source = "eventId")
    ParticipationRequestDto toDto(ParticipationRequest request);

    default String formatDate(java.time.LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return DateFormatter.format(dateTime);
    }
}
