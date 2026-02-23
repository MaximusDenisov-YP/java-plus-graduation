package ru.practicum.ewm.main.dto.request;

import lombok.*;
import ru.practicum.ewm.main.enums.RequestStatus;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateParticipationRequestDto {

    private List<Long> requestsId;

    private RequestStatus status;

}
