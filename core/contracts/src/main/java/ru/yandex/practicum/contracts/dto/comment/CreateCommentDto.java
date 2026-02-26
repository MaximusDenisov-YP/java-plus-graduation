package ru.yandex.practicum.contracts.dto.comment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentDto {
    private Long userId;

    private Long eventId;

    @NotNull
    private String text;
}
