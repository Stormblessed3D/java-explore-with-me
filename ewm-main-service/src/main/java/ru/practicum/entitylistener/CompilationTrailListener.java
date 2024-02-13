package ru.practicum.entitylistener;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.model.Compilation;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

@Slf4j
public class CompilationTrailListener {
    @PrePersist
    @PreUpdate
    @PreRemove
    protected void beforeAnyUpdate(Compilation compilation) {
        if (compilation.getId() == null || compilation.getId() == 0) {
            log.info("[COMPILATION AUDIT] About to add a compilation to database");
        } else {
            log.info("[COMPILATION AUDIT] About to update/delete compilation: {}", compilation.getId());
        }
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    protected void afterAnyUpdate(Compilation compilation) {
        log.info("[COMPILATION AUDIT] add/update/delete complete for compilation: {}", compilation.getId());
    }

    @PostLoad
    protected void afterLoad(Compilation compilation) {
        log.info("[COMPILATION AUDIT] compilation loaded from database: {}", compilation.getId());
    }
}
