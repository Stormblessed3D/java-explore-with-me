package ru.practicum.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventsAdminRequest {
    private String text;
    private List<Long> initiators;
    private List<EventState> states;
    private List<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean paid;
    private Integer from;
    private Integer size;

    public EventsAdminRequest(String text, List<Long> categories, List<Long> initiators, Boolean paid,
                              LocalDateTime rangeStart, LocalDateTime rangeEnd, List<EventState> states, Integer from,
                              Integer size) {
        this.text = text;
        this.categories = categories;
        this.initiators = initiators;
        this.paid = paid;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.states = states;
        this.from = from;
        this.size = size;
    }

    public boolean hasText() {
        return text != null;
    }

    public boolean hasCategories() {
        return categories != null;
    }

    public boolean hasInitiators() {
        return initiators != null;
    }

    public boolean hasPaid() {
        return paid != null;
    }

    public boolean hasStates() {
        return states != null;
    }
}
