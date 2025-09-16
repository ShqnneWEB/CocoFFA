/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityPickupItemEvent
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package hu.geri.listeners;

import hu.geri.CocoFFA;
import hu.geri.managers.VanishManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class VanishListener
implements Listener {
    private final CocoFFA plugin;

    public VanishListener(CocoFFA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!this.plugin.getSpectatorManager().isSpectating(player)) {
            VanishManager.handlePlayerQuit(player);
        }
        this.plugin.getSpectatorManager().handlePlayerQuit(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (VanishManager.isVanished(player) && (event.getItem() == null || !this.plugin.getSpectatorItemManager().isPlayerSelectorItem(event.getItem()) && !this.plugin.getSpectatorItemManager().isLeaveSpectateItem(event.getItem()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (VanishManager.isVanished(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (VanishManager.isVanished(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (VanishManager.isVanished(player) && !this.plugin.getSpectatorItemManager().isPlayerSelectorItem(event.getItemDrop().getItemStack()) && !this.plugin.getSpectatorItemManager().isLeaveSpectateItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (VanishManager.isVanished(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player player;
        if (event.getDamager() instanceof Player && VanishManager.isVanished(player = (Player)event.getDamager())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (VanishManager.isVanished(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (VanishManager.isVanished(player)) {
            String command = event.getMessage().toLowerCase();
            if (command.startsWith("/ffa leavespectate") || command.startsWith("/freeforall leavespectate") || command.startsWith("/" + this.plugin.getConfigManager().getMainCommand() + " leavespectate")) {
                return;
            }
            event.setCancelled(true);
            player.sendMessage(this.plugin.getLocaleManager().getMessage("spectator.command-blocked"));
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        Player player;
        if (event.getEntity() instanceof Player && VanishManager.isVanished(player = (Player)event.getEntity())) {
            event.setCancelled(true);
        }
    }
}

