/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 */
package hu.geri.libs.gui.components;

import org.bukkit.event.Event;

@FunctionalInterface
public interface GuiAction<T extends Event> {
    public void execute(T var1);
}

