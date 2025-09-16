/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.parser;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.Event;
import java.util.Iterator;

public interface Parser
extends Iterator<Event> {
    public boolean checkEvent(Event.ID var1);

    public Event peekEvent();

    @Override
    public Event next();
}

