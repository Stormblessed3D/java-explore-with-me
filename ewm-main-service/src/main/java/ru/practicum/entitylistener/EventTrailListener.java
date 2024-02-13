package ru.practicum.entitylistener;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.model.Event;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

@Slf4j
public class EventTrailListener {
    @PrePersist
    @PreUpdate
    @PreRemove
    protected void beforeAnyUpdate(Event event) {
        if (event.getId() == null || event.getId() == 0) {
            log.info("[EVENT AUDIT] About to add a event to database");
        } else {
            log.info("[EVENT AUDIT] About to update/delete event: {}", event.getId());
        }
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    protected void afterAnyUpdate(Event event) {
        log.info("[EVENT AUDIT] add/update/delete complete for event: {}", event.getId());
    }

    @PostLoad
    protected void afterLoad(Event event) {
        log.info("[EVENT AUDIT] event loaded from database: {}", event.getId());
    }
}
