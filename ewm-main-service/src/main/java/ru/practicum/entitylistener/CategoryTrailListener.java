package ru.practicum.entitylistener;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.model.Category;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

@Slf4j
public class CategoryTrailListener {
    @PrePersist
    @PreUpdate
    @PreRemove
    protected void beforeAnyUpdate(Category category) {
        if (category.getId() == null || category.getId() == 0) {
            log.info("[CATEGORY AUDIT] About to add a category to database");
        } else {
            log.info("[CATEGORY AUDIT] About to update/delete category: {}", category.getId());
        }
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    protected void afterAnyUpdate(Category category) {
        log.info("[CATEGORY AUDIT] add/update/delete complete for category: {}", category.getId());
    }

    @PostLoad
    protected void afterLoad(Category category) {
        log.info("[CATEGORY AUDIT] category loaded from database: {}", category.getId());
    }
}
