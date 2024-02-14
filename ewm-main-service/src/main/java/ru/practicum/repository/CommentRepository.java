package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.util.EventCommentsCount;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"event", "author"})
    List<Comment> findByEventIn(List<Event> events, Sort sort);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"event", "author"})
    List<Comment> findByEvent(Event event, Sort sort);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"event", "author"})
    List<Comment> findByAuthor(User user);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"event", "author"})
    Page<Comment> findByAuthorId(Long userId, PageRequest pageRequest);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"event", "author"})
    Page<Comment> findByEventId(Long eventId, PageRequest pageRequest);

    @Query("SELECT new ru.practicum.util.EventCommentsCount(e.id, COUNT(e.id)) " +
            "FROM Comment AS c " +
            "JOIN c.event as e " +
            "WHERE (e in ?1) " +
            "GROUP BY e.id")
    List<EventCommentsCount> countCommentsByEvent(List<Event> events, Sort sort);
}
