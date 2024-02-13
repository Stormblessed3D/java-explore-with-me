package ru.practicum.model.Qclasses;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;


/**
 * QCompilation is a Querydsl query type for Compilation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCompilation extends EntityPathBase<Compilation> {

    private static final long serialVersionUID = -1779695929L;

    public static final QCompilation compilation = new QCompilation("compilation");

    public final SetPath<Event, QEvent> events = this.<Event, QEvent>createSet("events", Event.class, QEvent.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath pinned = createBoolean("pinned");

    public final StringPath title = createString("title");

    public QCompilation(String variable) {
        super(Compilation.class, forVariable(variable));
    }

    public QCompilation(Path<? extends Compilation> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCompilation(PathMetadata metadata) {
        super(Compilation.class, metadata);
    }

}

