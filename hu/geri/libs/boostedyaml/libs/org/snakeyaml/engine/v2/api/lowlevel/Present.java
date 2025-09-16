/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.lowlevel;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.DumpSettings;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.lowlevel.StreamToStringWriter;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.emitter.Emitter;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.Event;
import java.util.Iterator;
import java.util.Objects;

public class Present {
    private final DumpSettings settings;

    public Present(DumpSettings settings) {
        Objects.requireNonNull(settings, "DumpSettings cannot be null");
        this.settings = settings;
    }

    public String emitToString(Iterator<Event> events) {
        Objects.requireNonNull(events, "events cannot be null");
        StreamToStringWriter writer = new StreamToStringWriter();
        Emitter emitter = new Emitter(this.settings, writer);
        events.forEachRemaining(emitter::emit);
        return writer.toString();
    }
}

