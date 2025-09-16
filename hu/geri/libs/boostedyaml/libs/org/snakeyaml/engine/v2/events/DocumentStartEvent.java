/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.SpecVersion;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.Event;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class DocumentStartEvent
extends Event {
    private final boolean explicit;
    private final Optional<SpecVersion> specVersion;
    private final Map<String, String> tags;

    public DocumentStartEvent(boolean explicit, Optional<SpecVersion> specVersion, Map<String, String> tags, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
        this.explicit = explicit;
        Objects.requireNonNull(specVersion);
        this.specVersion = specVersion;
        Objects.requireNonNull(tags);
        this.tags = tags;
    }

    public DocumentStartEvent(boolean explicit, Optional<SpecVersion> specVersion, Map<String, String> tags) {
        this(explicit, specVersion, tags, Optional.empty(), Optional.empty());
    }

    public boolean isExplicit() {
        return this.explicit;
    }

    public Optional<SpecVersion> getSpecVersion() {
        return this.specVersion;
    }

    public Map<String, String> getTags() {
        return this.tags;
    }

    @Override
    public Event.ID getEventId() {
        return Event.ID.DocumentStart;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("+DOC");
        if (this.isExplicit()) {
            builder.append(" ---");
        }
        return builder.toString();
    }
}

