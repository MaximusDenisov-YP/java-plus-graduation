package ru.yandex.practicum.contracts.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.yandex.practicum.contracts.enums.RequestStatus;
import ru.yandex.practicum.contracts.util.DateFormatter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatter.PATTERN)
    private String created;

    private Long requester;

    private Long event;

    private RequestStatus status;
}
