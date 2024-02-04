package ru.practicum.ewm;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode()
@Builder
@NoArgsConstructor
public class StatsDtoResponse {
    private String app;
    private String uri;
    private Long hits;

    public StatsDtoResponse(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}
