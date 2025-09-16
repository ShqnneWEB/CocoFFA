/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.libs.universalScheduler;

import hu.geri.libs.universalScheduler.bukkitScheduler.BukkitScheduler;
import hu.geri.libs.universalScheduler.foliaScheduler.FoliaScheduler;
import hu.geri.libs.universalScheduler.paperScheduler.PaperScheduler;
import hu.geri.libs.universalScheduler.scheduling.schedulers.TaskScheduler;
import hu.geri.libs.universalScheduler.utils.JavaUtil;
import org.bukkit.plugin.Plugin;

public class UniversalScheduler {
    public static final boolean isFolia = JavaUtil.classExists("io.papermc.paper.threadedregions.RegionizedServer");
    public static final boolean isCanvas = JavaUtil.classExists("io.canvasmc.canvas.server.ThreadedServer");
    public static final boolean isExpandedSchedulingAvailable = JavaUtil.classExists("io.papermc.paper.threadedregions.scheduler.ScheduledTask");

    public static TaskScheduler getScheduler(Plugin plugin) {
        return isFolia || isCanvas ? new FoliaScheduler(plugin) : (isExpandedSchedulingAvailable ? new PaperScheduler(plugin) : new BukkitScheduler(plugin));
    }
}

