/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.lowlevel;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.emitter.Emitable;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.Event;
import java.util.ArrayList;
import java.util.List;

class EmitableEvents
implements Emitable {
    private final List<Event> events = new ArrayList<Event>();

    EmitableEvents() {
    }

    @Override
    public void emit(Event event) {
        this.events.add(event);
    }

    public List<Event> getEvents() {
        return this.events;
    }
}

