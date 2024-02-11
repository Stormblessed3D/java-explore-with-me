package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.StatsDtoRequest;
import ru.practicum.ewm.StatsDtoResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@PropertySource("classpath:stats-application.properties")
public class StatisticsClientImpl implements  StatisticsClient {
    private final RestTemplate restTemplate;
    private static final String STATS_BASE_URL = "/stats";
    private static final String HITS_BASE_URL = "/hit";
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatisticsClientImpl(@Value("${ewm-stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    @Override
    public List<StatsDtoResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String encodedStartTime = encodeValue(start.format(DATE_TIME_FORMATTER));
        String encodedEndTime = encodeValue(end.format(DATE_TIME_FORMATTER));
        String encodedUris = encodeValue(String.join(", ", uris));
        Map<String, Object> parameters = Map.of(
                "start", encodedStartTime,
                "end", encodedEndTime,
                "uris", encodedUris,
                "unique", unique
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<List<StatsDtoResponse>> requestEntity = new HttpEntity<>(headers);
        String fullPath = STATS_BASE_URL + "?start={start}&end={end}&uris={uris}&unique={unique}";
        ResponseEntity<List<StatsDtoResponse>> responseEntity =
                restTemplate.exchange(fullPath, HttpMethod.GET, requestEntity,
                        new ParameterizedTypeReference<List<StatsDtoResponse>>() {
                        }, parameters);
        List<StatsDtoResponse> statsDtos = responseEntity.getBody();
        return statsDtos;
    }

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @Override
    public void saveHit(StatsDtoRequest statsDtoRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<StatsDtoRequest> requestEntity = new HttpEntity<>(statsDtoRequest, headers);
        String fullPath = HITS_BASE_URL;
        restTemplate.exchange(fullPath, HttpMethod.POST, requestEntity, Void.class);
    }
}
