/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.comments.CommentType;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.Event;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Objects;
import java.util.Optional;

public final class CommentEvent
extends Event {
    private final CommentType type;
    private final String value;

    public CommentEvent(CommentType type, String value, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
        Objects.requireNonNull(type);
        this.type = type;
        Objects.requireNonNull(value);
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public CommentType getCommentType() {
        return this.type;
    }

    @Override
    public Event.ID getEventId() {
        return Event.ID.Comment;
    }

    public String toString() {
        String builder = "=COM " + (Object)((Object)this.type) + " " + this.value;
        return builder;
    }
}

