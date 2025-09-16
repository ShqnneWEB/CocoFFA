/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.WorldBorder
 *  org.bukkit.entity.Player
 */
package hu.geri.managers;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.arena.BorderPhase;
import hu.geri.libs.universalScheduler.scheduling.tasks.MyScheduledTask;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

public class BorderManager {
    private final CocoFFA plugin;
    private final Map<Arena, MyScheduledTask> borderTasks;

    public BorderManager(CocoFFA plugin) {
        this.plugin = plugin;
        this.borderTasks = new HashMap<Arena, MyScheduledTask>();
    }

    public void setDefaultBorder(Arena arena) {
        Location borderCenter = arena.getBorderCenterLocation();
        if (borderCenter == null || borderCenter.getWorld() == null) {
            return;
        }
        this.plugin.getUniversalScheduler().runTask(borderCenter, () -> {
            World world = borderCenter.getWorld();
            WorldBorder border = world.getWorldBorder();
            border.setCenter(borderCenter);
            border.setSize((double)arena.getDefaultBorderSize());
        });
    }

    public void startBorderSystem(Arena arena) {
        if (arena.getBorderPhases().isEmpty()) {
            return;
        }
        this.stopBorderSystem(arena);
        for (BorderPhase phase : arena.getBorderPhases().values()) {
            phase.setApplied(false);
        }
        this.setDefaultBorder(arena);
        long gameStartTime = System.currentTimeMillis();
        MyScheduledTask task = this.plugin.getUniversalScheduler().runTaskTimer(() -> {
            int elapsed = (int)((System.currentTimeMillis() - gameStartTime) / 1000L);
            for (Map.Entry<Integer, BorderPhase> entry : arena.getBorderPhases().entrySet()) {
                BorderPhase phase = entry.getValue();
                if (phase.getStartTime() != elapsed || phase.isApplied()) continue;
                this.applyBorderPhase(arena, phase);
                phase.setApplied(true);
            }
        }, 0L, 20L);
        this.borderTasks.put(arena, task);
    }

    private void applyBorderPhase(Arena arena, BorderPhase phase) {
        Location borderCenter = arena.getBorderCenterLocation();
        if (borderCenter == null || borderCenter.getWorld() == null) {
            return;
        }
        this.plugin.getUniversalScheduler().runTask(borderCenter, () -> {
            World world = borderCenter.getWorld();
            WorldBorder border = world.getWorldBorder();
            border.setSize((double)phase.getSize(), (long)phase.getSeconds());
        });
        String borderMessage = this.plugin.getLocaleManager().getMessage("arena.border-broadcast").replace("%size%", String.valueOf(phase.getSize())).replace("%time%", String.valueOf(phase.getSeconds()));
        for (Player player : arena.getPlayers()) {
            player.sendMessage(borderMessage);
        }
    }

    public void stopBorderSystem(Arena arena) {
        Location borderCenter;
        MyScheduledTask task = this.borderTasks.remove(arena);
        if (task != null) {
            task.cancel();
        }
        if ((borderCenter = arena.getBorderCenterLocation()) != null && borderCenter.getWorld() != null) {
            this.plugin.getUniversalScheduler().runTask(borderCenter, () -> {
                World world = borderCenter.getWorld();
                WorldBorder border = world.getWorldBorder();
                border.setCenter(borderCenter);
                border.setSize((double)arena.getDefaultBorderSize());
            });
        }
    }

    public void resetBorder(Arena arena) {
        this.setDefaultBorder(arena);
    }
}

