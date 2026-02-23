package ru.practicum.ewm.main.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.main.dto.event.EventFullDto;

import java.util.List;

@Data
@Builder
public class CompilationDto {

    @NotNull
    private Long id;

    @NotBlank
    private String title;

    private Boolean pinned;

    private List<EventFullDto> events;

}
