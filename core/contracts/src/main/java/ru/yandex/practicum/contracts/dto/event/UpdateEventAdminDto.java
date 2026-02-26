package ru.yandex.practicum.contracts.dto.event;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.contracts.enums.AdminStateAction;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminDto {

    @Size(min = 20, max = 2000)
    private String annotation;
    private Long category;

    @Size(min = 20, max = 7000)
    private String description;
    private String eventDate;
    private LocationDto location;
    private Boolean paid;

    @Positive
    private Integer participantLimit;
    private Boolean requestModeration;
    private AdminStateAction stateAction;

    @Size(min = 3, max = 120)
    private String title;
}
