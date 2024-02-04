package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class StatsClient extends BaseClient {
    private static final String API_PREFIX = "/stats";
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Autowired
    public StatsClient(@Value("${ewm-stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, String uris, boolean unique) {
        String encodedStartTime = encodeValue(start.format(DATE_TIME_FORMATTER));
        String encodedEndTime = encodeValue(end.format(DATE_TIME_FORMATTER));
        Map<String, Object> parameters = Map.of(
                "start", encodedStartTime,
                "end", encodedEndTime,
                "uris", uris,
                "unique", unique
        );
        return get("?start={encodedStartTime}&end={encodedEndTime}&uris={uris}&unique={unique}", parameters);
    }

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
