package ru.yandex.practicum.extraservice.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.contracts.dto.compilation.CompilationDto;
import ru.yandex.practicum.contracts.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.contracts.dto.compilation.UpdateCompilationRequest;
import ru.yandex.practicum.contracts.dto.event.EventFullDto;
import ru.yandex.practicum.contracts.exception.NotFoundException;
import ru.yandex.practicum.contracts.feign.events.EventsClient;
import ru.yandex.practicum.extraservice.entity.Compilation;
import ru.yandex.practicum.extraservice.mapper.CompilationMapper;
import ru.yandex.practicum.extraservice.repository.CompilationRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventsClient eventsClient;
    private final CompilationMapper compilationMapper;

    @Override
    public List<CompilationDto> getCompilations(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").descending());
        Page<Compilation> compilations = compilationRepository.findAll(pageable);
        Set<Long> eventsIds = new HashSet<>();
        compilations.getContent().forEach(compilation -> eventsIds.addAll(compilation.getEvents()));
        List<EventFullDto> eventFullDtos = eventsClient.getEventsByIds(eventsIds);

        return compilations.getContent()
                .stream()
                .map(compilation -> compilationMapper.toCompilationDto(
                        compilation,
                        eventFullDtos.stream()
                                .filter(eventFullDto -> compilation.getEvents()
                                        .contains(eventFullDto.getId()))
                                .toList()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository
                .findById(compId).orElseThrow(() -> new NotFoundException("подборка не найдена"));
        List<EventFullDto> eventFullDtos = new ArrayList<>();
        if (!compilation.getEvents().isEmpty()) {
            eventFullDtos = eventsClient.getEventsByIds(compilation.getEvents());
        }
        return compilationMapper.toCompilationDto(compilation, eventFullDtos);
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<EventFullDto> events = new ArrayList<>();
        if (newCompilationDto.hasEvents()) {
            events = eventsClient.getEventsByIds(newCompilationDto.getEvents());;
        }
        if (!newCompilationDto.hasPinned()) {
            newCompilationDto.setPinned(false);
        }
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        Compilation newCompilation = compilationRepository.save(compilation);
        return compilationMapper.toCompilationDto(newCompilation, events);
    }

    @Override
    public void deleteCompilationById(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilationById(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("подборка не найдена"));

        compilationMapper.updateCompilationFields(updateCompilationRequest, compilation);
        List<EventFullDto> eventFullDtos = new ArrayList<>();
        if (updateCompilationRequest.hasEvents()) {
            eventFullDtos = eventsClient.getEventsByIds(updateCompilationRequest.getEvents());
            compilation.setEvents(updateCompilationRequest.getEvents());
        }

        Compilation updatedCompilation = compilationRepository.save(compilation);
        return compilationMapper.toCompilationDto(updatedCompilation, eventFullDtos);
    }

    // TODO: Вот здесь надо получить List<Long> eventIds из eventsClient и использовать в методах выше
//    private Set<Long> getEventsFromIds(Set<Long> eventIds) {
//        if (eventIds == null || eventIds.isEmpty()) {
//            return new HashSet<>();
//        }
//        return eventIds.stream()
//                .map(eventsClient::getEventById)
//                .collect(Collectors.toCollection(ArrayList::new));
//    }

}