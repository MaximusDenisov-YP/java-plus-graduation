package ru.practicum.ewm.main.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UpdateCompilationRequest {

    @Size(min = 1, max = 50)
    private String title;

    private Boolean pinned;

    private Set<Long> events;

    public boolean hasTitle() {
        return this.title != null && !this.title.isBlank();
    }

    public boolean hasPinned() {
        return this.pinned != null;
    }

    public boolean hasEvents() {
        return this.events != null && !this.events.isEmpty();
    }

}