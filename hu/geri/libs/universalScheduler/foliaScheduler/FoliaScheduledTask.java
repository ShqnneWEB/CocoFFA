/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.papermc.paper.threadedregions.scheduler.ScheduledTask
 *  io.papermc.paper.threadedregions.scheduler.ScheduledTask$ExecutionState
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.libs.universalScheduler.foliaScheduler;

import hu.geri.libs.universalScheduler.scheduling.tasks.MyScheduledTask;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.Plugin;

public class FoliaScheduledTask
implements MyScheduledTask {
    private final ScheduledTask task;

    public FoliaScheduledTask(ScheduledTask task) {
        this.task = task;
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
        return this.task.getOwningPlugin();
    }

    @Override
    public boolean isCurrentlyRunning() {
        ScheduledTask.ExecutionState state = this.task.getExecutionState();
        return state == ScheduledTask.ExecutionState.RUNNING || state == ScheduledTask.ExecutionState.CANCELLED_RUNNING;
    }

    @Override
    public boolean isRepeatingTask() {
        return this.task.isRepeatingTask();
    }
}

