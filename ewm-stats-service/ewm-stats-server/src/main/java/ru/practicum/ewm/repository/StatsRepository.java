package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.StatsDtoResponse;
import ru.practicum.ewm.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("SELECT new ru.practicum.ewm.StatsDtoResponse(s.app, s.uri, COUNT(distinct s.app)) " +
            "FROM Stats AS s WHERE s.timestamp BETWEEN ?1 AND ?2 GROUP BY s.app, s.uri, s.ip ORDER BY COUNT(s.app) DESC")
    List<StatsDtoResponse> countHitsByUniqueIpAndDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.ewm.StatsDtoResponse(s.app, s.uri, COUNT(s.app)) " +
            "FROM Stats AS s WHERE s.timestamp BETWEEN ?1 AND ?2 GROUP BY s.app, s.uri, s.ip ORDER BY COUNT(s.app) DESC")
    List<StatsDtoResponse> countHitsByNonUniqueIpAndDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.ewm.StatsDtoResponse(s.app, s.uri, COUNT(distinct s.app)) " +
            "FROM Stats AS s WHERE (s.timestamp BETWEEN ?1 AND ?2) AND (s.uri in ?3) GROUP BY s.app, s.uri, s.ip " +
            "ORDER BY COUNT(s.app) DESC")
    List<StatsDtoResponse> countHitsByUniqueIpAndDateBetweenAndUrisIn(LocalDateTime start, LocalDateTime end,
                                                                      List<String> uris);

    @Query("SELECT new ru.practicum.ewm.StatsDtoResponse(s.app, s.uri, COUNT(s.app)) " +
            "FROM Stats AS s WHERE (s.timestamp BETWEEN ?1 AND ?2) AND (s.uri in ?3) GROUP BY s.app, s.uri, s.ip " +
            "ORDER BY COUNT(s.app) DESC")
    List<StatsDtoResponse> countHitsByNonUniqueIpAndDateBetweenAndUrisIn(LocalDateTime start, LocalDateTime end,
                                                                         List<String> uris);
}