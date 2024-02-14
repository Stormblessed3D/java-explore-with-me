package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.model.EventState;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class EventFullDto {
    @EqualsAndHashCode.Include
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private EventState state;
    private String title;
    private Long views;
    private Long numberOfComments;

    public EventFullDto(Long id, String annotation, CategoryDto category, Long confirmedRequests, LocalDateTime createdOn,
                        String description, LocalDateTime eventDate, UserShortDto initiator, Location location,
                        Boolean paid, Integer participantLimit, LocalDateTime publishedOn, Boolean requestModeration,
                        EventState state, String title, Long views, Long numberOfComments) {
        this.id = id;
        this.annotation = annotation;
        this.category = category;
        this.confirmedRequests = confirmedRequests;
        this.createdOn = createdOn;
        this.description = description;
        this.eventDate = eventDate;
        this.initiator = initiator;
        this.location = location;
        this.paid = paid;
        this.participantLimit = Objects.requireNonNullElse(participantLimit, 0);
        this.publishedOn = publishedOn;
        this.requestModeration = Objects.requireNonNullElse(requestModeration, true);
        this.state = state;
        this.title = title;
        this.views = views;
        this.numberOfComments = numberOfComments;
    }
}
