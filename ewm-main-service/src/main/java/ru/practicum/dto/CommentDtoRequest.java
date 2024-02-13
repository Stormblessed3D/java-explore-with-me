package ru.practicum.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class CommentDtoRequest {
    @NotBlank
    @Size(min = 1, max = 500)
    private String text;

    public CommentDtoRequest() {
    }

    public CommentDtoRequest(String text) {
        this.text = text;
    }
}