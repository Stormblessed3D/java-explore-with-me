package ru.practicum.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventCommentsCount {
    private Long eventId;
    private Long numberOfComments;
}
