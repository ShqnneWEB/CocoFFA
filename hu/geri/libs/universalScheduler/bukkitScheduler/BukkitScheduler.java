/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.libs.universalScheduler.bukkitScheduler;

import hu.geri.libs.universalScheduler.bukkitScheduler.BukkitScheduledTask;
import hu.geri.libs.universalScheduler.scheduling.schedulers.TaskScheduler;
import hu.geri.libs.universalScheduler.scheduling.tasks.MyScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class BukkitScheduler
implements TaskScheduler {
    final Plugin plugin;

    public BukkitScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isGlobalThread() {
        return Bukkit.getServer().isPrimaryThread();
    }

    @Override
    public boolean isEntityThread(Entity entity) {
        return Bukkit.getServer().isPrimaryThread();
    }

    @Override
    public boolean isRegionThread(Location location) {
        return Bukkit.getServer().isPrimaryThread();
    }

    @Override
    public MyScheduledTask runTask(Runnable runnable) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTask(this.plugin, runnable));
    }

    @Override
    public MyScheduledTask runTaskLater(Runnable runnable, long delay) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskLater(this.plugin, runnable, delay));
    }

    @Override
    public MyScheduledTask runTaskTimer(Runnable runnable, long delay, long period) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskTimer(this.plugin, runnable, delay, period));
    }

    @Override
    public MyScheduledTask runTaskAsynchronously(Runnable runnable) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable));
    }

    @Override
    public MyScheduledTask runTaskLaterAsynchronously(Runnable runnable, long delay) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, runnable, delay));
    }

    @Override
    public MyScheduledTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, runnable, delay, period));
    }

    @Override
    public MyScheduledTask runTask(Plugin plugin, Runnable runnable) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTask(plugin, runnable));
    }

    @Override
    public MyScheduledTask runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskLater(plugin, runnable, delay));
    }

    @Override
    public MyScheduledTask runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period));
    }

    @Override
    public MyScheduledTask runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable));
    }

    @Override
    public MyScheduledTask runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay));
    }

    @Override
    public MyScheduledTask runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long delay, long period) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period));
    }

    @Override
    public void execute(Runnable runnable) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, runnable);
    }

    @Override
    public void cancelTasks() {
        Bukkit.getScheduler().cancelTasks(this.plugin);
    }

    @Override
    public void cancelTasks(Plugin plugin) {
        Bukkit.getScheduler().cancelTasks(plugin);
    }
}

