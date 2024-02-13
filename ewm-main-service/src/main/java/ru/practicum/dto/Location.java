package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @NotNull
    @Min(-90)
    @Max(90)
    @Valid
    private Double lat;
    @NotNull
    @Min(-180)
    @Max(180)
    @Valid
    private Double lon;
}
