package ru.practicum.ewm.listener;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.model.Stats;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

@Slf4j

public class StatsTrailListener {
    @PrePersist
    @PreUpdate
    @PreRemove
    protected void beforeAnyUpdate(Stats stats) {
        if (stats.getId() == null || stats.getId() == 0) {
            log.info("[STATS AUDIT] About to add a stats to database");
        } else {
            log.info("[STATS AUDIT] About to update/delete stats: {}", stats.getId());
        }
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    protected void afterAnyUpdate(Stats stats) {
        log.info("[STATS AUDIT] add/update/delete complete for stats: {}", stats.getId());
    }

    @PostLoad
    protected void afterLoad(Stats stats) {
        log.info("[STATS AUDIT] stats loaded from database: {}", stats.getId());
    }
}
