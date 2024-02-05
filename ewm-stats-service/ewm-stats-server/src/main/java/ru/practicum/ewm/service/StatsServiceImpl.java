package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsDtoRequest;
import ru.practicum.ewm.StatsDtoResponse;
import ru.practicum.ewm.mapper.StatsMapper;
import ru.practicum.ewm.model.Stats;
import ru.practicum.ewm.repository.StatsRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<StatsDtoResponse> getStats(LocalDateTime start, LocalDateTime end, Optional<List<String>> uris,
                                           Boolean unique) {
        if (end.isEqual(start) || end.isBefore(start)) {
            throw new ValidationException("Ошибка валидации - дата конца диапазона выгрузки равна или меньше" +
                    "даты начала диапазона");
        }
        if (unique) {
            if (uris.isEmpty()) {
                return statsRepository.countHitsByUniqueIpAndDateBetween(start, end);
            } else {
                return statsRepository.countHitsByUniqueIpAndDateBetweenAndUrisIn(start, end, uris.get());
            }
        }
        if (uris.isEmpty()) {
            return statsRepository.countHitsByNonUniqueIpAndDateBetween(start, end);
        } else {
            return statsRepository.countHitsByNonUniqueIpAndDateBetweenAndUrisIn(start, end, uris.get());
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void createHit(StatsDtoRequest statsDtoRequest) {
        Stats stats = statsMapper.statsDtoRequestToStats(statsDtoRequest);
        statsRepository.save(stats);
    }
}
