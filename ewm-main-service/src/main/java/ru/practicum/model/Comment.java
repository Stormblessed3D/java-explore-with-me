package ru.practicum.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.entitylistener.CommentTrailListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@EntityListeners(CommentTrailListener.class)
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    @EqualsAndHashCode.Include
    private Long id;
    private String text;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id",  referencedColumnName = "event_id", nullable = false)
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", referencedColumnName = "user_id")
    @ToString.Exclude
    private User author;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;

    public Comment(Long id, String text, Event event, User author, LocalDateTime createdOn, LocalDateTime modifiedOn) {
        this.id = id;
        this.text = text;
        this.event = event;
        this.author = author;
        this.createdOn = createdOn;
        this.modifiedOn = modifiedOn;
    }

    public Comment(Long id, String text, Event event, User author, LocalDateTime createdOn) {
        this.id = id;
        this.text = text;
        this.event = event;
        this.author = author;
        this.createdOn = createdOn;
        this.modifiedOn = null;
    }
}
