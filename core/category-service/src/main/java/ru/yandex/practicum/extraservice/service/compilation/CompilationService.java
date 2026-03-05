package ru.yandex.practicum.extraservice.service.compilation;

import ru.yandex.practicum.contracts.dto.compilation.CompilationDto;
import ru.yandex.practicum.contracts.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.contracts.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getCompilations(int page, int size);

    CompilationDto getCompilationById(Long compId);

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Long compId);

    CompilationDto updateCompilationById(Long compId, UpdateCompilationRequest updateCompilationRequest);

}
