/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.libs.universalScheduler.scheduling.tasks;

import org.bukkit.plugin.Plugin;

public interface MyScheduledTask {
    public void cancel();

    public boolean isCancelled();

    public Plugin getOwningPlugin();

    public boolean isCurrentlyRunning();

    public boolean isRepeatingTask();
}

