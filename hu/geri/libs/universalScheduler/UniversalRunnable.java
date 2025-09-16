/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.libs.universalScheduler;

import hu.geri.libs.universalScheduler.UniversalScheduler;
import hu.geri.libs.universalScheduler.scheduling.tasks.MyScheduledTask;
import org.bukkit.plugin.Plugin;

public abstract class UniversalRunnable
implements Runnable {
    MyScheduledTask task;

    public synchronized void cancel() throws IllegalStateException {
        this.checkScheduled();
        this.task.cancel();
    }

    public synchronized boolean isCancelled() throws IllegalStateException {
        this.checkScheduled();
        return this.task.isCancelled();
    }

    public synchronized MyScheduledTask runTask(Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        this.checkNotYetScheduled();
        return this.setupTask(UniversalScheduler.getScheduler(plugin).runTask(this));
    }

    public synchronized MyScheduledTask runTaskAsynchronously(Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        this.checkNotYetScheduled();
        return this.setupTask(UniversalScheduler.getScheduler(plugin).runTaskAsynchronously(this));
    }

    public synchronized MyScheduledTask runTaskLater(Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        this.checkNotYetScheduled();
        return this.setupTask(UniversalScheduler.getScheduler(plugin).runTaskLater(this, delay));
    }

    public synchronized MyScheduledTask runTaskLaterAsynchronously(Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        this.checkNotYetScheduled();
        return this.setupTask(UniversalScheduler.getScheduler(plugin).runTaskLaterAsynchronously(this, delay));
    }

    public synchronized MyScheduledTask runTaskTimer(Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        this.checkNotYetScheduled();
        return this.setupTask(UniversalScheduler.getScheduler(plugin).runTaskTimer(this, delay, period));
    }

    public synchronized MyScheduledTask runTaskTimerAsynchronously(Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        this.checkNotYetScheduled();
        return this.setupTask(UniversalScheduler.getScheduler(plugin).runTaskTimerAsynchronously(this, delay, period));
    }

    private void checkScheduled() {
        if (this.task == null) {
            throw new IllegalStateException("Not scheduled yet");
        }
    }

    private void checkNotYetScheduled() {
        if (this.task != null) {
            throw new IllegalStateException("Already scheduled");
        }
    }

    private MyScheduledTask setupTask(MyScheduledTask task) {
        this.task = task;
        return task;
    }
}

