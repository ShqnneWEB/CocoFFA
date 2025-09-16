/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.CollectionEndEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.Event;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Optional;

public final class SequenceEndEvent
extends CollectionEndEvent {
    public SequenceEndEvent(Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
    }

    public SequenceEndEvent() {
    }

    @Override
    public Event.ID getEventId() {
        return Event.ID.SequenceEnd;
    }

    public String toString() {
        return "-SEQ";
    }
}

