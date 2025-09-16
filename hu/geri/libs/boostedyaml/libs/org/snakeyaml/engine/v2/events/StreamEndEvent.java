/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.Event;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Optional;

public final class StreamEndEvent
extends Event {
    public StreamEndEvent(Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
    }

    public StreamEndEvent() {
    }

    @Override
    public Event.ID getEventId() {
        return Event.ID.StreamEnd;
    }

    public String toString() {
        return "-STR";
    }
}

