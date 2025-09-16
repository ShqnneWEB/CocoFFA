/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.Event;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Optional;

public abstract class CollectionEndEvent
extends Event {
    public CollectionEndEvent(Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
    }

    public CollectionEndEvent() {
    }
}

