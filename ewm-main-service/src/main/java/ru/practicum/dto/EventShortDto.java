package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EventShortDto {
    @EqualsAndHashCode.Include
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;

    public EventShortDto(String annotation, CategoryDto category, LocalDateTime eventDate, UserShortDto initiator,
                         Boolean paid, String title) {
        this.annotation = annotation;
        this.category = category;
        this.eventDate = eventDate;
        this.initiator = initiator;
        this.paid = paid;
        this.title = title;
    }
}
