package ru.practicum.model.Qclasses;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;


/**
 * QEvent is a Querydsl query type for Event
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEvent extends EntityPathBase<Event> {

    private static final long serialVersionUID = -1082030434L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEvent event = new QEvent("event");

    public final StringPath annotation = createString("annotation");

    public final QCategory category;

    public final DateTimePath<java.time.LocalDateTime> createdOn = createDateTime("createdOn", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final DateTimePath<java.time.LocalDateTime> eventDate = createDateTime("eventDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QUser initiator;

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final BooleanPath paid = createBoolean("paid");

    public final NumberPath<Integer> participantLimit = createNumber("participantLimit", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> publishedOn = createDateTime("publishedOn", java.time.LocalDateTime.class);

    public final BooleanPath requestModeration = createBoolean("requestModeration");

    public final EnumPath<EventState> state = createEnum("state", EventState.class);

    public final StringPath title = createString("title");

    public QEvent(String variable) {
        this(Event.class, forVariable(variable), INITS);
    }

    public QEvent(Path<? extends Event> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEvent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEvent(PathMetadata metadata, PathInits inits) {
        this(Event.class, metadata, inits);
    }

    public QEvent(Class<? extends Event> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category")) : null;
        this.initiator = inits.isInitialized("initiator") ? new QUser(forProperty("initiator")) : null;
    }

}

