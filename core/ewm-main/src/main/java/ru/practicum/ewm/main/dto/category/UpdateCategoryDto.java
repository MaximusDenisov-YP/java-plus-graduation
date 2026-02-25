package ru.practicum.ewm.main.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryDto {

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

}
