package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;

import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    @Query("select count(*) " +
            "from Event as e " +
            "join e.category as c " +
            "where c.id = ?1")
    Long countByCategory(Long itemId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"category", "initiator"})
    Page<Event> findAllByInitiatorId(Long userId, PageRequest pageRequest);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"category", "initiator"})
    Optional<Event> findByIdAndState(Long eventId, EventState eventState);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"category", "initiator"})
    Set<Event> findAllByIdIn(Set<Long> events);
}
