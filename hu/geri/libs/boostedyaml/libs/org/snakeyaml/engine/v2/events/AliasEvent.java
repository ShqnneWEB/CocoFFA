/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.Anchor;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.Event;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.NodeEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Optional;

public final class AliasEvent
extends NodeEvent {
    private final Anchor alias;

    public AliasEvent(Optional<Anchor> anchor, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(anchor, startMark, endMark);
        this.alias = anchor.orElseThrow(() -> new NullPointerException("Anchor is required in AliasEvent"));
    }

    public AliasEvent(Optional<Anchor> anchor) {
        this(anchor, Optional.empty(), Optional.empty());
    }

    @Override
    public Event.ID getEventId() {
        return Event.ID.Alias;
    }

    public String toString() {
        return "=ALI *" + this.alias;
    }

    public Anchor getAlias() {
        return this.alias;
    }
}

