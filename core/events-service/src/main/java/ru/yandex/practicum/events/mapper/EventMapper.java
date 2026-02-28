package ru.yandex.practicum.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.contracts.dto.category.CategoryDto;
import ru.yandex.practicum.contracts.dto.event.EventFullDto;
import ru.yandex.practicum.contracts.dto.event.EventShortDto;
import ru.yandex.practicum.contracts.dto.event.LocationDto;
import ru.yandex.practicum.contracts.dto.event.NewEventDto;
import ru.yandex.practicum.contracts.dto.user.UserShortDto;
import ru.yandex.practicum.contracts.enums.EventState;
import ru.yandex.practicum.contracts.util.DateFormatter;
import ru.yandex.practicum.events.entity.Event;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
//        uses = {CategoryMapper.class, UserMapper.class, LocationMapper.class},
        imports = {LocalDateTime.class, EventState.class})
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", expression = "java(LocalDateTime.now())")
    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "confirmedRequests", constant = "0L")
    @Mapping(target = "views", constant = "0L")
    @Mapping(target = "paid", source = "newEventDto.paid", defaultExpression = "java(false)")
    @Mapping(target = "participantLimit", source = "newEventDto.participantLimit", defaultExpression = "java(0)")
    @Mapping(target = "requestModeration", source = "newEventDto.requestModeration", defaultExpression = "java(true)")
    @Mapping(target = "eventDate", expression = "java(parseDate(newEventDto.getEventDate()))")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "categoryId", source = "newEventDto.category")
//    Event toEvent(NewEventDto newEventDto, Category category, User initiator,
//                  ru.practicum.ewm.main.dto.event.LocationDto location);
    Event toEvent(NewEventDto newEventDto, Long initiatorId, LocationDto location);

    @Mapping(target = "eventDate", expression = "java(formatDate(event.getEventDate()))")
    @Mapping(target = "createdOn", expression = "java(formatDate(event.getCreatedOn()))")
    @Mapping(target = "publishedOn", expression = "java(formatDate(event.getPublishedOn()))")
    @Mapping(target = "state", expression = "java(event.getState().name())")
    @Mapping(target = "id", source = "event.id")
    EventFullDto toEventFullDto(Event event, UserShortDto initiator, CategoryDto category);

    @Mapping(target = "eventDate", expression = "java(formatDate(event.getEventDate()))")
    EventShortDto toEventShortDto(Event event);

    default String formatDate(LocalDateTime dateTime) {
        return DateFormatter.format(dateTime);
    }

    default LocalDateTime parseDate(String dateString) {
        return DateFormatter.parse(dateString);
    }

//    @AfterMapping
//    default void setDefaultValues(@MappingTarget Event.EventBuilder eventBuilder, NewEventDto newEventDto) {
//        if (newEventDto.getPaid() == null) {
//            eventBuilder.paid(false);
//        }
//        if (newEventDto.getParticipantLimit() == null) {
//            eventBuilder.participantLimit(0);
//        }
//        if (newEventDto.getRequestModeration() == null) {
//            eventBuilder.requestModeration(true);
//        }
//    }
}