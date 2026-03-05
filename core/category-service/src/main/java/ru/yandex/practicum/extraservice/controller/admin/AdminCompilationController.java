package ru.yandex.practicum.extraservice.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contracts.dto.compilation.CompilationDto;
import ru.yandex.practicum.contracts.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.contracts.dto.compilation.UpdateCompilationRequest;
import ru.yandex.practicum.extraservice.service.compilation.CompilationService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        CompilationDto compilationDto = compilationService.createCompilation(newCompilationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(compilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationById(@PathVariable("compId") Long compId) {
        compilationService.deleteCompilationById(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilationById(@PathVariable("compId") Long compId,
                                                @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return compilationService.updateCompilationById(compId, updateCompilationRequest);
    }
}