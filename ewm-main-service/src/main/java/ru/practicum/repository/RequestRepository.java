package ru.practicum.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.RequestStatus;
import ru.practicum.util.RequestShortCount;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    @Query("select count(*) " +
            "from ParticipationRequest as pr " +
            "join pr.event as e " +
            "where (e.id = ?1) and (pr.status = ?2)")
    Long countByRequesterIdAndStatus(Long eventId, RequestStatus status);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"event", "requester"})
    List<ParticipationRequest> findAllByEventInAndStatus(List<Event> events, RequestStatus status);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"event", "requester"})
    List<ParticipationRequest> findAllByEventId(Long eventId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"event", "requester"})
    List<ParticipationRequest> findAllByIdIn(List<Long> requestIds);

    @Query("select count(*) " +
            "from ParticipationRequest as pr " +
            "join pr.event as e " +
            "where e.id = ?1")
    Long countByEventId(Long eventId);

    @Query("select count(*) " +
            "from ParticipationRequest as pr " +
            "join pr.event as e " +
            "where (e.id = ?1) and (pr.status = ?2)")
    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"event", "requester"})
    List<ParticipationRequest> findAllByRequesterId(Long userId);

    @Query("select case when count(pr)> 0 then true else false end from ParticipationRequest pr " +
            "join pr.event as e " +
            "join pr.requester as r " +
            "where r.id = ?1 and e.id = ?2")
    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);


    @Query("SELECT new ru.practicum.util.RequestShortCount(pr.event.id, COUNT(pr.event.id)) " +
            "FROM ParticipationRequest AS pr " +
            "JOIN pr.event as e " +
            "WHERE (e in ?1) AND (pr.status = ?2) " +
            "GROUP BY pr.event.id")
    List<RequestShortCount> countByEventInAndStatus(List<Event> events, RequestStatus status);
}
