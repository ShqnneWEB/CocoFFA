/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.listeners;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.arena.ArenaState;
import hu.geri.libs.universalScheduler.UniversalScheduler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class ArenaListener
implements Listener {
    private final CocoFFA plugin;

    public ArenaListener(CocoFFA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }
        Player victim = (Player)event.getEntity();
        Player attacker = (Player)event.getDamager();
        String victimArena = this.plugin.getArenaManager().getPlayerArena(victim);
        String attackerArena = this.plugin.getArenaManager().getPlayerArena(attacker);
        if (victimArena == null || attackerArena == null || !victimArena.equals(attackerArena)) {
            return;
        }
        Arena arena = this.plugin.getArenaManager().getArena(victimArena);
        if (arena == null) {
            return;
        }
        if (arena.getState() != ArenaState.STARTED) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (this.plugin.getSpectatorManager().isSpectating(player)) {
            this.plugin.getSpectatorManager().handlePlayerDeath(player);
            return;
        }
        String arenaName = this.plugin.getArenaManager().getPlayerArena(player);
        if (arenaName == null) {
            return;
        }
        Arena arena = this.plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            return;
        }
        event.getDrops().clear();
        event.setDroppedExp(0);
        Player killer = player.getKiller();
        if (killer != null && arena.getPlayers().contains(killer)) {
            arena.addKill(killer);
            this.plugin.getWebhookManager().sendKillWebhook(player.getName(), killer.getName(), arena.getDisplayName());
        }
        UniversalScheduler.getScheduler((Plugin)this.plugin).runTaskLater((Entity)player, () -> {
            player.spigot().respawn();
            UniversalScheduler.getScheduler((Plugin)this.plugin).runTaskLater((Entity)player, () -> {
                this.plugin.getArenaManager().leaveArena(player);
                String eliminationMessage = this.plugin.getLocaleManager().getMessage("arena.player.eliminated", "{player}", player.getName());
                for (Player arenaPlayer : arena.getPlayers()) {
                    arenaPlayer.sendMessage(eliminationMessage);
                }
            }, 1L);
        }, 1L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.plugin.getInventoryManager().handlePlayerJoin(player);
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.getSpectatorManager().isSpectating(player)) {
            this.plugin.getSpectatorManager().handlePlayerQuit(player);
            return;
        }
        String arenaName = this.plugin.getArenaManager().getPlayerArena(player);
        if (arenaName == null) {
            return;
        }
        Arena arena = this.plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            return;
        }
        this.plugin.getInventoryManager().restoreOfflinePlayerInventory(player);
        this.plugin.getArenaManager().leaveArena(player);
        String eliminationMessage = this.plugin.getLocaleManager().getMessage("arena.player.eliminated", "{player}", player.getName());
        for (Player arenaPlayer : arena.getPlayers()) {
            arenaPlayer.sendMessage(eliminationMessage);
        }
    }
}

