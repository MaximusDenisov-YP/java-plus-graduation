package ru.practicum.ewm.main.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.main.enums.RequestStatus;
import ru.practicum.ewm.main.util.DateFormatter;

@Data
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
