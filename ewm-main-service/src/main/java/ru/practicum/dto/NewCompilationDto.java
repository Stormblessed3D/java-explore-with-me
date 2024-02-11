package ru.practicum.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class NewCompilationDto {
    private Set<Long> events = new HashSet<>();
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
    private Boolean pinned = false;

    public NewCompilationDto(Set<Long> events, Boolean pinned, String title) {
        if (events != null) {
            this.events = events;
        }
        if (pinned != null) {
            this.pinned = pinned;
        }
        this.title = title;
    }
}
