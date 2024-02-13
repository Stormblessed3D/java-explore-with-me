package ru.practicum.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.entitylistener.ParticipationRequestTrailListener;

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
@Table(name = "requests")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(ParticipationRequestTrailListener.class)
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    @EqualsAndHashCode.Include
    private Long id;
    private LocalDateTime created;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id",  referencedColumnName = "event_id", nullable = false)
    @ToString.Exclude
    private Event event;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id",  referencedColumnName = "user_id", nullable = false)
    @ToString.Exclude
    private User requester;
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public ParticipationRequest(Long id, LocalDateTime created, Event event, User requester, RequestStatus status) {
        this.id = id;
        this.created = Objects.requireNonNullElseGet(created, LocalDateTime::now);
        this.event = event;
        this.requester = requester;
        this.status = Objects.requireNonNullElse(status, RequestStatus.PENDING);
    }

    public ParticipationRequest(LocalDateTime created, Event event, User requester, RequestStatus status) {
        this.created = Objects.requireNonNullElseGet(created, LocalDateTime::now);
        this.event = event;
        this.requester = requester;
        this.status = Objects.requireNonNullElse(status, RequestStatus.PENDING);
    }

    public ParticipationRequest(LocalDateTime created, Event event, User requester) {
        this.created = Objects.requireNonNullElseGet(created, LocalDateTime::now);
        this.event = event;
        this.requester = requester;
    }
}
