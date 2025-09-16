/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package hu.geri.libs.universalScheduler.bukkitScheduler;

import hu.geri.libs.universalScheduler.scheduling.tasks.MyScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitScheduledTask
implements MyScheduledTask {
    BukkitTask task;
    boolean isRepeating;

    public BukkitScheduledTask(BukkitTask task) {
        this.task = task;
        this.isRepeating = false;
    }

    public BukkitScheduledTask(BukkitTask task, boolean isRepeating) {
        this.task = task;
        this.isRepeating = isRepeating;
    }

    @Override
    public void cancel() {
        this.task.cancel();
    }

    @Override
    public boolean isCancelled() {
        return this.task.isCancelled();
    }

    @Override
    public Plugin getOwningPlugin() {
        return this.task.getOwner();
    }

    @Override
    public boolean isCurrentlyRunning() {
        return Bukkit.getServer().getScheduler().isCurrentlyRunning(this.task.getTaskId());
    }

    @Override
    public boolean isRepeatingTask() {
        return this.isRepeating;
    }
}

