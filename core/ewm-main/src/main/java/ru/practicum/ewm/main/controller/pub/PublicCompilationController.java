package ru.practicum.ewm.main.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.service.compilation.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        return compilationService.getCompilations(from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable("compId") Long compId) {
        CompilationDto compilationDto = compilationService.getCompilationById(compId);
        return compilationDto;
    }

}