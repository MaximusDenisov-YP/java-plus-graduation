package ru.yandex.practicum.contracts.dto.event;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.contracts.dto.category.CategoryDto;
import ru.yandex.practicum.contracts.dto.user.UserShortDto;

import static ru.yandex.practicum.contracts.util.DateFormatter.PATTERN;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {

    private Long id;

    @NotBlank
    private String annotation;

    @NotNull
    private CategoryDto category;


    private Long confirmedRequests;

    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN)
    private String eventDate;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private Boolean paid;

    @NotNull
    private String title;

    private Long views;
}
