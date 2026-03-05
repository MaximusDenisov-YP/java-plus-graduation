package ru.yandex.practicum.contracts.dto.event;

import lombok.*;
import ru.yandex.practicum.contracts.dto.request.ParticipationRequestDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateParticipationRequestListDto {

    private List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();

    private List<ParticipationRequestDto> rejectedRequests =  new ArrayList<>();

}