package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsDtoRequest;
import ru.practicum.ewm.StatsDtoResponse;
import ru.practicum.ewm.mapper.StatsMapper;
import ru.practicum.ewm.model.Stats;
import ru.practicum.ewm.repository.StatsRepository;

import javax.validation.ValidationException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.ewm.constants.ConstantKeeper.DATE_TIME_FORMATTER;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<StatsDtoResponse> getStats(String start, String end, Optional<List<String>> uris,
                                           Boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), DATE_TIME_FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), DATE_TIME_FORMATTER);
        List<String> decodedUris = new ArrayList<>();
        if (uris.isPresent()) {
            List<String> uris1 = uris.get();
            decodedUris = uris1.stream()
                    .map(u -> URLDecoder.decode(u, StandardCharsets.UTF_8))
                    .collect(Collectors.toList());
        }
        if (startTime.isAfter(endTime)) {
            throw new ValidationException("Ошибка валидации - дата конца диапазона выгрузки равна или меньше" +
                    "даты начала диапазона");
        }
        if (unique) {
            if (uris.isEmpty()) {
                return statsRepository.countHitsByUniqueIpAndDateBetween(startTime, endTime);
            } else {
                return statsRepository.countHitsByUniqueIpAndDateBetweenAndUrisIn(startTime,
                        endTime, decodedUris);
            }
        }
        if (uris.isEmpty()) {
            return statsRepository.countHitsByNonUniqueIpAndDateBetween(startTime, endTime);
        } else {
            return statsRepository.countHitsByNonUniqueIpAndDateBetweenAndUrisIn(startTime, endTime, decodedUris);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void createHit(StatsDtoRequest statsDtoRequest) {
        Stats stats = statsMapper.statsDtoRequestToStats(statsDtoRequest);
        statsRepository.save(stats);
    }
}
