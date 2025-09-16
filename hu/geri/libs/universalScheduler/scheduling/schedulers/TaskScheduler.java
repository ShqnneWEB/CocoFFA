/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.libs.universalScheduler.scheduling.schedulers;

import hu.geri.libs.universalScheduler.scheduling.tasks.MyScheduledTask;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public interface TaskScheduler {
    public boolean isGlobalThread();

    default public boolean isTickThread() {
        return Bukkit.getServer().isPrimaryThread();
    }

    public boolean isEntityThread(Entity var1);

    public boolean isRegionThread(Location var1);

    public MyScheduledTask runTask(Runnable var1);

    public MyScheduledTask runTaskLater(Runnable var1, long var2);

    public MyScheduledTask runTaskTimer(Runnable var1, long var2, long var4);

    @Deprecated
    default public MyScheduledTask runTask(Plugin plugin, Runnable runnable) {
        return this.runTask(runnable);
    }

    @Deprecated
    default public MyScheduledTask runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        return this.runTaskLater(runnable, delay);
    }

    @Deprecated
    default public MyScheduledTask runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period) {
        return this.runTaskTimer(runnable, delay, period);
    }

    default public MyScheduledTask runTask(Location location, Runnable runnable) {
        return this.runTask(runnable);
    }

    default public MyScheduledTask runTaskLater(Location location, Runnable runnable, long delay) {
        return this.runTaskLater(runnable, delay);
    }

    default public MyScheduledTask runTaskTimer(Location location, Runnable runnable, long delay, long period) {
        return this.runTaskTimer(runnable, delay, period);
    }

    @Deprecated
    default public MyScheduledTask scheduleSyncDelayedTask(Runnable runnable, long delay) {
        return this.runTaskLater(runnable, delay);
    }

    @Deprecated
    default public MyScheduledTask scheduleSyncDelayedTask(Runnable runnable) {
        return this.runTask(runnable);
    }

    @Deprecated
    default public MyScheduledTask scheduleSyncRepeatingTask(Runnable runnable, long delay, long period) {
        return this.runTaskTimer(runnable, delay, period);
    }

    default public MyScheduledTask runTask(Entity entity, Runnable runnable) {
        return this.runTask(runnable);
    }

    default public MyScheduledTask runTaskLater(Entity entity, Runnable runnable, long delay) {
        return this.runTaskLater(runnable, delay);
    }

    default public MyScheduledTask runTaskTimer(Entity entity, Runnable runnable, long delay, long period) {
        return this.runTaskTimer(runnable, delay, period);
    }

    public MyScheduledTask runTaskAsynchronously(Runnable var1);

    public MyScheduledTask runTaskLaterAsynchronously(Runnable var1, long var2);

    public MyScheduledTask runTaskTimerAsynchronously(Runnable var1, long var2, long var4);

    @Deprecated
    default public MyScheduledTask runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        return this.runTaskAsynchronously(runnable);
    }

    @Deprecated
    default public MyScheduledTask runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay) {
        return this.runTaskLaterAsynchronously(runnable, delay);
    }

    @Deprecated
    default public MyScheduledTask runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long delay, long period) {
        return this.runTaskTimerAsynchronously(runnable, delay, period);
    }

    default public <T> Future<T> callSyncMethod(Callable<T> task) {
        CompletableFuture completableFuture = new CompletableFuture();
        this.execute(() -> {
            try {
                completableFuture.complete(task.call());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return completableFuture;
    }

    public void execute(Runnable var1);

    default public void execute(Location location, Runnable runnable) {
        this.execute(runnable);
    }

    default public void execute(Entity entity, Runnable runnable) {
        this.execute(runnable);
    }

    public void cancelTasks();

    public void cancelTasks(Plugin var1);
}

