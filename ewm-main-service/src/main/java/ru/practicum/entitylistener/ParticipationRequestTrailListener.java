package ru.practicum.entitylistener;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.model.ParticipationRequest;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

@Slf4j
public class ParticipationRequestTrailListener {
    @PrePersist
    @PreUpdate
    @PreRemove
    protected void beforeAnyUpdate(ParticipationRequest participationRequest) {
        if (participationRequest.getId() == null || participationRequest.getId() == 0) {
            log.info("[PARTICIPATION REQUEST AUDIT] About to add a participation request to database");
        } else {
            log.info("[PARTICIPATION REQUEST AUDIT] About to update/delete participation request: {}",
                    participationRequest.getId());
        }
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    protected void afterAnyUpdate(ParticipationRequest participationRequest) {
        log.info("[PARTICIPATION REQUEST AUDIT] add/update/delete complete for participation request: {}",
                participationRequest.getId());
    }

    @PostLoad
    protected void afterLoad(ParticipationRequest participationRequest) {
        log.info("[PARTICIPATION REQUEST AUDIT] participation request loaded from database: {}",
                participationRequest.getId());
    }
}
