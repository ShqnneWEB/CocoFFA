/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerDropItemEvent
 */
package hu.geri.listeners;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.arena.ArenaState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemDropListener
implements Listener {
    private final CocoFFA plugin;

    public ItemDropListener(CocoFFA plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        boolean preventInGameDrop;
        boolean preventCountdownDrop;
        String bypassPermission;
        Player player = event.getPlayer();
        if (player.hasPermission(bypassPermission = this.plugin.getConfig().getString("permissions.item-drop-bypass.permission", "cocoffa.itemdrop.bypass"))) {
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
        ArenaState state = arena.getState();
        if (state == ArenaState.WAITING && (preventCountdownDrop = this.plugin.getConfig().getBoolean("item-dropping.countdown.enabled", true))) {
            event.setCancelled(true);
            return;
        }
        if (state == ArenaState.STARTED && (preventInGameDrop = this.plugin.getConfig().getBoolean("item-dropping.in-game.enabled", false))) {
            event.setCancelled(true);
            return;
        }
    }
}

