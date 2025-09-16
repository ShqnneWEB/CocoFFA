/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.WorldBorder
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package hu.geri.listeners;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.libs.universalScheduler.scheduling.tasks.MyScheduledTask;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BorderTeleportListener
implements Listener {
    private final CocoFFA plugin;
    private final Map<UUID, MyScheduledTask> countdownTasks;
    private final Map<UUID, Integer> countdownTimers;

    public BorderTeleportListener(CocoFFA plugin) {
        this.plugin = plugin;
        this.countdownTasks = new HashMap<UUID, MyScheduledTask>();
        this.countdownTimers = new HashMap<UUID, Integer>();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!this.plugin.getConfigManager().isBorderTeleportEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        String arenaName = null;
        arenaName = this.plugin.getSpectatorManager().isSpectating(player) ? this.plugin.getSpectatorManager().getSpectatingArena(player) : this.plugin.getArenaManager().getPlayerArena(player);
        if (arenaName == null) {
            return;
        }
        Arena arena = this.plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            return;
        }
        Location borderCenter = arena.getBorderCenterLocation();
        if (borderCenter == null) {
            return;
        }
        Location playerLoc = player.getLocation();
        if (playerLoc.getWorld() == null || !playerLoc.getWorld().equals((Object)borderCenter.getWorld())) {
            return;
        }
        WorldBorder border = playerLoc.getWorld().getWorldBorder();
        double size = border.getSize();
        double halfSize = size / 2.0;
        Location center = border.getCenter();
        double deltaX = Math.abs(playerLoc.getX() - center.getX());
        double deltaZ = Math.abs(playerLoc.getZ() - center.getZ());
        int extraBlocks = this.plugin.getConfigManager().getBorderTeleportBlocks();
        double allowedDistance = halfSize + (double)extraBlocks;
        UUID playerUUID = player.getUniqueId();
        if (deltaX > allowedDistance || deltaZ > allowedDistance) {
            if (!this.countdownTasks.containsKey(playerUUID)) {
                this.startCountdown(player, arena);
            }
        } else {
            this.cancelCountdown(playerUUID);
        }
    }

    private void startCountdown(Player player, Arena arena) {
        UUID playerUUID = player.getUniqueId();
        this.cancelCountdown(playerUUID);
        int seconds = this.plugin.getConfigManager().getBorderTeleportSeconds();
        this.countdownTimers.put(playerUUID, seconds);
        MyScheduledTask task = this.plugin.getUniversalScheduler().runTaskTimer((Entity)player, () -> {
            if (!player.isOnline()) {
                this.cancelCountdown(playerUUID);
                return;
            }
            String currentArena = this.plugin.getArenaManager().getPlayerArena(player);
            if (currentArena == null || !currentArena.equals(arena.getName())) {
                this.cancelCountdown(playerUUID);
                return;
            }
            Integer remainingSecondsObj = this.countdownTimers.get(playerUUID);
            if (remainingSecondsObj == null) {
                this.cancelCountdown(playerUUID);
                return;
            }
            int remainingSeconds = remainingSecondsObj;
            if (remainingSeconds <= 0) {
                this.teleportPlayerBack(player, arena);
                this.cancelCountdown(playerUUID);
            } else {
                String message = this.plugin.getLocaleManager().getMessage("border.countdown", "{seconds}", String.valueOf(remainingSeconds));
                player.sendMessage(message);
                this.countdownTimers.put(playerUUID, remainingSeconds - 1);
            }
        }, 0L, 20L);
        this.countdownTasks.put(playerUUID, task);
    }

    private void cancelCountdown(UUID playerUUID) {
        MyScheduledTask task = this.countdownTasks.remove(playerUUID);
        if (task != null) {
            task.cancel();
        }
        this.countdownTimers.remove(playerUUID);
    }

    private void teleportPlayerBack(Player player, Arena arena) {
        Location borderCenter = arena.getBorderCenterLocation();
        if (borderCenter != null) {
            Location teleportLoc = borderCenter.clone();
            teleportLoc.setY(player.getLocation().getY());
            ((CompletableFuture)player.teleportAsync(teleportLoc).thenAccept(result -> {
                if (result.booleanValue() && player.isOnline()) {
                    String message = this.plugin.getLocaleManager().getMessage("border.teleported");
                    player.sendMessage(message);
                }
            })).exceptionally(throwable -> {
                this.plugin.getLogger().warning("Failed to teleport player " + player.getName() + " back to border center: " + throwable.getMessage());
                return null;
            });
        }
    }

    public void cleanup() {
        for (MyScheduledTask task : this.countdownTasks.values()) {
            if (task == null) continue;
            task.cancel();
        }
        this.countdownTasks.clear();
        this.countdownTimers.clear();
    }

    public void cancelPlayerCountdown(Player player) {
        this.cancelCountdown(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.cancelCountdown(event.getPlayer().getUniqueId());
    }
}

