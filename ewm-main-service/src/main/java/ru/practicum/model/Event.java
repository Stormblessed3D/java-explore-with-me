package ru.practicum.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.entitylistener.EventTrailListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(EventTrailListener.class)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    @EqualsAndHashCode.Include
    private Long id;
    private String title;
    private String annotation;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id",  referencedColumnName = "category_id", nullable = false)
    @ToString.Exclude
    private Category category;
    @Enumerated(EnumType.STRING)
    private EventState state;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    private Double latitude;
    private Double longitude;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "initiator_id",  referencedColumnName = "user_id", nullable = false)
    @ToString.Exclude
    private User initiator;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    private Boolean paid;
    @Column(name = "request_moderation")
    private Boolean requestModeration;

    public Event(Long id, String title, String annotation, String description, Category category, EventState state,
                 LocalDateTime createdOn, LocalDateTime eventDate, LocalDateTime publishedOn, Double latitude,
                 Double longitude, User initiator, Integer participantLimit, Boolean paid,
                 Boolean requestModeration) {
        this.id = id;
        this.title = title;
        this.annotation = annotation;
        this.description = description;
        this.category = category;
        this.state = Objects.requireNonNullElse(state, EventState.PENDING);
        this.createdOn = Objects.requireNonNullElseGet(createdOn, LocalDateTime::now);
        this.eventDate = eventDate;
        this.publishedOn = Objects.requireNonNullElseGet(publishedOn, LocalDateTime::now);
        this.latitude = latitude;
        this.longitude = longitude;
        this.initiator = initiator;
        this.participantLimit = participantLimit;
        this.paid = paid;
        this.requestModeration = Objects.requireNonNullElse(requestModeration, true);
    }
}
