/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.papermc.paper.threadedregions.scheduler.AsyncScheduler
 *  io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler
 *  io.papermc.paper.threadedregions.scheduler.RegionScheduler
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.libs.universalScheduler.foliaScheduler;

import hu.geri.libs.universalScheduler.foliaScheduler.FoliaScheduledTask;
import hu.geri.libs.universalScheduler.scheduling.schedulers.TaskScheduler;
import hu.geri.libs.universalScheduler.scheduling.tasks.MyScheduledTask;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class FoliaScheduler
implements TaskScheduler {
    final Plugin plugin;
    private final RegionScheduler regionScheduler = Bukkit.getServer().getRegionScheduler();
    private final GlobalRegionScheduler globalRegionScheduler = Bukkit.getServer().getGlobalRegionScheduler();
    private final AsyncScheduler asyncScheduler = Bukkit.getServer().getAsyncScheduler();

    public FoliaScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isGlobalThread() {
        return Bukkit.getServer().isGlobalTickThread();
    }

    @Override
    public boolean isTickThread() {
        return Bukkit.getServer().isPrimaryThread();
    }

    @Override
    public boolean isEntityThread(Entity entity) {
        return Bukkit.getServer().isOwnedByCurrentRegion(entity);
    }

    @Override
    public boolean isRegionThread(Location location) {
        return Bukkit.getServer().isOwnedByCurrentRegion(location);
    }

    @Override
    public MyScheduledTask runTask(Runnable runnable) {
        return new FoliaScheduledTask(this.globalRegionScheduler.run(this.plugin, task -> runnable.run()));
    }

    @Override
    public MyScheduledTask runTaskLater(Runnable runnable, long delay) {
        if (delay <= 0L) {
            return this.runTask(runnable);
        }
        return new FoliaScheduledTask(this.globalRegionScheduler.runDelayed(this.plugin, task -> runnable.run(), delay));
    }

    @Override
    public MyScheduledTask runTaskTimer(Runnable runnable, long delay, long period) {
        delay = this.getOneIfNotPositive(delay);
        return new FoliaScheduledTask(this.globalRegionScheduler.runAtFixedRate(this.plugin, task -> runnable.run(), delay, period));
    }

    @Override
    public MyScheduledTask runTask(Plugin plugin, Runnable runnable) {
        return new FoliaScheduledTask(this.globalRegionScheduler.run(plugin, task -> runnable.run()));
    }

    @Override
    public MyScheduledTask runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        if (delay <= 0L) {
            return this.runTask(plugin, runnable);
        }
        return new FoliaScheduledTask(this.globalRegionScheduler.runDelayed(plugin, task -> runnable.run(), delay));
    }

    @Override
    public MyScheduledTask runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period) {
        delay = this.getOneIfNotPositive(delay);
        return new FoliaScheduledTask(this.globalRegionScheduler.runAtFixedRate(plugin, task -> runnable.run(), delay, period));
    }

    @Override
    public MyScheduledTask runTask(Location location, Runnable runnable) {
        return new FoliaScheduledTask(this.regionScheduler.run(this.plugin, location, task -> runnable.run()));
    }

    @Override
    public MyScheduledTask runTaskLater(Location location, Runnable runnable, long delay) {
        if (delay <= 0L) {
            return this.runTask(runnable);
        }
        return new FoliaScheduledTask(this.regionScheduler.runDelayed(this.plugin, location, task -> runnable.run(), delay));
    }

    @Override
    public MyScheduledTask runTaskTimer(Location location, Runnable runnable, long delay, long period) {
        delay = this.getOneIfNotPositive(delay);
        return new FoliaScheduledTask(this.regionScheduler.runAtFixedRate(this.plugin, location, task -> runnable.run(), delay, period));
    }

    @Override
    public MyScheduledTask runTask(Entity entity, Runnable runnable) {
        return new FoliaScheduledTask(entity.getScheduler().run(this.plugin, task -> runnable.run(), null));
    }

    @Override
    public MyScheduledTask runTaskLater(Entity entity, Runnable runnable, long delay) {
        if (delay <= 0L) {
            return this.runTask(entity, runnable);
        }
        return new FoliaScheduledTask(entity.getScheduler().runDelayed(this.plugin, task -> runnable.run(), null, delay));
    }

    @Override
    public MyScheduledTask runTaskTimer(Entity entity, Runnable runnable, long delay, long period) {
        delay = this.getOneIfNotPositive(delay);
        return new FoliaScheduledTask(entity.getScheduler().runAtFixedRate(this.plugin, task -> runnable.run(), null, delay, period));
    }

    @Override
    public MyScheduledTask runTaskAsynchronously(Runnable runnable) {
        return new FoliaScheduledTask(this.asyncScheduler.runNow(this.plugin, task -> runnable.run()));
    }

    @Override
    public MyScheduledTask runTaskLaterAsynchronously(Runnable runnable, long delay) {
        delay = this.getOneIfNotPositive(delay);
        return new FoliaScheduledTask(this.asyncScheduler.runDelayed(this.plugin, task -> runnable.run(), delay * 50L, TimeUnit.MILLISECONDS));
    }

    @Override
    public MyScheduledTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period) {
        return new FoliaScheduledTask(this.asyncScheduler.runAtFixedRate(this.plugin, task -> runnable.run(), delay * 50L, period * 50L, TimeUnit.MILLISECONDS));
    }

    @Override
    public MyScheduledTask runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        return new FoliaScheduledTask(this.asyncScheduler.runNow(plugin, task -> runnable.run()));
    }

    @Override
    public MyScheduledTask runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay) {
        delay = this.getOneIfNotPositive(delay);
        return new FoliaScheduledTask(this.asyncScheduler.runDelayed(plugin, task -> runnable.run(), delay * 50L, TimeUnit.MILLISECONDS));
    }

    @Override
    public MyScheduledTask runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long delay, long period) {
        delay = this.getOneIfNotPositive(delay);
        return new FoliaScheduledTask(this.asyncScheduler.runAtFixedRate(plugin, task -> runnable.run(), delay * 50L, period * 50L, TimeUnit.MILLISECONDS));
    }

    @Override
    public void execute(Runnable runnable) {
        this.globalRegionScheduler.execute(this.plugin, runnable);
    }

    @Override
    public void execute(Location location, Runnable runnable) {
        this.regionScheduler.execute(this.plugin, location, runnable);
    }

    @Override
    public void execute(Entity entity, Runnable runnable) {
        entity.getScheduler().execute(this.plugin, runnable, null, 1L);
    }

    @Override
    public void cancelTasks() {
        this.globalRegionScheduler.cancelTasks(this.plugin);
        this.asyncScheduler.cancelTasks(this.plugin);
    }

    @Override
    public void cancelTasks(Plugin plugin) {
        this.globalRegionScheduler.cancelTasks(plugin);
        this.asyncScheduler.cancelTasks(plugin);
    }

    private long getOneIfNotPositive(long x) {
        return x <= 0L ? 1L : x;
    }
}

