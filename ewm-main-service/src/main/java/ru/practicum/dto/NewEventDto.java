package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotNull
    @Positive
    private Long category;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @NotNull
    @FutureOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    private Boolean paid;
    @Min(0)
    private Integer participantLimit;
    private Boolean requestModeration;
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    public NewEventDto(String annotation, Long category, String description, LocalDateTime eventDate, Location location,
                       Boolean paid, Integer participantLimit, Boolean requestModeration, String title) {
        this.annotation = annotation;
        this.category = category;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.paid = Objects.requireNonNullElse(paid, false);
        this.participantLimit = Objects.requireNonNullElse(participantLimit, 0);
        this.requestModeration = Objects.requireNonNullElse(requestModeration, true);
        this.title = title;
    }
}
