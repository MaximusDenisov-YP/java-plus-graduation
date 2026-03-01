package ru.yandex.practicum.extraservice.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.contracts.dto.compilation.CompilationDto;
import ru.yandex.practicum.contracts.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.contracts.dto.compilation.UpdateCompilationRequest;
import ru.yandex.practicum.contracts.dto.event.EventFullDto;
import ru.yandex.practicum.extraservice.entity.Compilation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "events", source = "events")
    CompilationDto toCompilationDto(Compilation compilation, List<EventFullDto> events);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "newCompilationDto.title")
    @Mapping(target = "pinned", source = "newCompilationDto.pinned")
//    @Mapping(target = "events", source = "events")
    Compilation toCompilation(NewCompilationDto newCompilationDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "events", ignore = true)
    void updateCompilationFields(UpdateCompilationRequest updateCompilationRequest,
                                 @MappingTarget Compilation compilation);
}
