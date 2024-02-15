package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class CommentDtoResponse {
    @EqualsAndHashCode.Include
    private Long id;
    private String text;
    private String authorName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedOn;

    public CommentDtoResponse() {
    }

    public CommentDtoResponse(Long id, String text, String authorName, LocalDateTime createdOn, LocalDateTime modifiedOn) {
        this.id = id;
        this.text = text;
        this.authorName = authorName;
        this.createdOn = createdOn;
        this.modifiedOn = modifiedOn;
    }
}
