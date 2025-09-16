/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package hu.geri.managers;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.managers.VanishManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpectatorManager {
    private final CocoFFA plugin;
    private final Map<UUID, String> spectatingArenas;
    private final Map<String, Set<UUID>> arenaSpectators;

    public SpectatorManager(CocoFFA plugin) {
        this.plugin = plugin;
        this.spectatingArenas = new HashMap<UUID, String>();
        this.arenaSpectators = new HashMap<String, Set<UUID>>();
    }

    public void addSpectator(Player player, Arena arena) {
        this.plugin.getInventoryManager().savePlayerInventory(player);
        player.getInventory().clear();
        player.setGameMode(GameMode.CREATIVE);
        if (!arena.getPlayers().isEmpty()) {
            Player randomPlayer = arena.getPlayers().iterator().next();
            player.teleport(randomPlayer.getLocation());
        } else if (arena.getStartLocation() != null) {
            player.teleport(arena.getStartLocation());
        }
        this.spectatingArenas.put(player.getUniqueId(), arena.getName());
        this.arenaSpectators.computeIfAbsent(arena.getName(), k -> new HashSet()).add(player.getUniqueId());
        VanishManager.addVanished(player);
        this.giveSpectatorItems(player);
    }

    public void removeSpectator(Player player) {
        Arena arena;
        Set<UUID> spectators;
        UUID playerUUID = player.getUniqueId();
        String arenaName = this.spectatingArenas.remove(playerUUID);
        if (arenaName != null && (spectators = this.arenaSpectators.get(arenaName)) != null) {
            spectators.remove(playerUUID);
            if (spectators.isEmpty()) {
                this.arenaSpectators.remove(arenaName);
            }
        }
        VanishManager.removeVanished(player);
        String exitGamemodeString = this.plugin.getConfigManager().getSpectatorExitGamemode();
        try {
            GameMode exitGamemode = GameMode.valueOf((String)exitGamemodeString.toUpperCase());
            player.setGameMode(exitGamemode);
        } catch (IllegalArgumentException e) {
            this.plugin.getLogger().warning("Invalid exit gamemode in config: " + exitGamemodeString + ". Using SURVIVAL as fallback.");
            player.setGameMode(GameMode.SURVIVAL);
        }
        this.plugin.getInventoryManager().restorePlayerInventory(player, false);
        if (arenaName != null && (arena = this.plugin.getArenaManager().getArena(arenaName)) != null && arena.getExitLocation() != null) {
            player.teleport(arena.getExitLocation());
        }
    }

    public boolean isSpectating(Player player) {
        return this.spectatingArenas.containsKey(player.getUniqueId());
    }

    public String getSpectatingArena(Player player) {
        return this.spectatingArenas.get(player.getUniqueId());
    }

    public Set<UUID> getArenaSpectators(String arenaName) {
        return this.arenaSpectators.getOrDefault(arenaName, new HashSet());
    }

    public void removeAllSpectatorsFromArena(String arenaName) {
        HashSet<UUID> spectators = new HashSet<UUID>(this.getArenaSpectators(arenaName));
        for (UUID spectatorUUID : spectators) {
            Player spectator = Bukkit.getPlayer((UUID)spectatorUUID);
            if (spectator == null || !spectator.isOnline()) continue;
            this.removeSpectator(spectator);
            spectator.sendMessage(this.plugin.getLocaleManager().getMessage("commands.spectate.arena-ended", "{arena_name}", arenaName));
        }
        this.arenaSpectators.remove(arenaName);
    }

    private void giveSpectatorItems(Player player) {
        ItemStack leaveItem;
        ItemStack playerSelector = this.plugin.getSpectatorItemManager().createPlayerSelectorItem();
        if (playerSelector != null) {
            player.getInventory().setItem(4, playerSelector);
        }
        if ((leaveItem = this.plugin.getSpectatorItemManager().createLeaveSpectateItem()) != null) {
            player.getInventory().setItem(8, leaveItem);
        }
    }

    public void handlePlayerQuit(Player player) {
        if (this.isSpectating(player)) {
            Set<UUID> spectators;
            this.plugin.getInventoryManager().restoreOfflinePlayerInventory(player);
            UUID playerUUID = player.getUniqueId();
            String arenaName = this.spectatingArenas.remove(playerUUID);
            if (arenaName != null && (spectators = this.arenaSpectators.get(arenaName)) != null) {
                spectators.remove(playerUUID);
                if (spectators.isEmpty()) {
                    this.arenaSpectators.remove(arenaName);
                }
            }
            VanishManager.handlePlayerQuit(player);
        }
    }

    public void handlePlayerDeath(Player player) {
        if (this.isSpectating(player)) {
            this.removeSpectator(player);
        }
    }
}

