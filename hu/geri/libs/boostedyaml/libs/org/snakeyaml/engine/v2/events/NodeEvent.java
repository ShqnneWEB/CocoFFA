/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.Anchor;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.Event;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Objects;
import java.util.Optional;

public abstract class NodeEvent
extends Event {
    private final Optional<Anchor> anchor;

    public NodeEvent(Optional<Anchor> anchor, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
        Objects.requireNonNull(anchor);
        this.anchor = anchor;
    }

    public Optional<Anchor> getAnchor() {
        return this.anchor;
    }
}

