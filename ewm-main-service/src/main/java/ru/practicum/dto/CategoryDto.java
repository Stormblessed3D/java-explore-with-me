package ru.practicum.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class CategoryDto {
    @EqualsAndHashCode.Include
    private Long id;
    private String name;

    public CategoryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
