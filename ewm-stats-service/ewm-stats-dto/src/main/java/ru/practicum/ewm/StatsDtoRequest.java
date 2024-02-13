package ru.practicum.ewm;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
@Builder
@NoArgsConstructor
public class StatsDtoRequest {
    @NotBlank
    @Size(max = 255)
    private String app;
    @NotBlank
    @Size(max = 255)
    private String uri;
    @NotBlank
    @Size(max = 255)
    private String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public StatsDtoRequest(String app, String uri, String ip, LocalDateTime timestamp) {
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timestamp = timestamp;
    }

    public StatsDtoRequest(String app) {
        this.app = app;
    }
}
