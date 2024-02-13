package ru.practicum.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestShortCount {
    private Long eventId;
    private Long count;
}
