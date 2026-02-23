package ru.practicum.ewm.main.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;

    private String text;

    private String ownerName;

    private Long event;

    private LocalDateTime created;
}
