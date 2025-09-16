/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 */
package hu.geri.listeners;

import hu.geri.CocoFFA;
import hu.geri.managers.VanishManager;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpectatorItemListener
implements Listener {
    private final CocoFFA plugin;

    public SpectatorItemListener(CocoFFA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (VanishManager.isVanished(player)) {
            ItemStack dropped = event.getItemDrop().getItemStack();
            if (this.plugin.getSpectatorItemManager().isPlayerSelectorItem(dropped) || this.plugin.getSpectatorItemManager().isLeaveSpectateItem(dropped)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!VanishManager.isVanished(player)) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item == null) {
                return;
            }
            if (this.plugin.getSpectatorItemManager().isPlayerSelectorItem(item)) {
                event.setCancelled(true);
                this.plugin.getSpectatorPlayerSelectorGUI().openPlayerSelector(player);
            } else if (this.plugin.getSpectatorItemManager().isLeaveSpectateItem(item)) {
                event.setCancelled(true);
                String arenaName = this.plugin.getSpectatorManager().getSpectatingArena(player);
                this.plugin.getSpectatorManager().removeSpectator(player);
                player.sendMessage(this.plugin.getLocaleManager().getMessage("commands.leavespectate.success", "{arena_name}", arenaName != null ? arenaName : "Unknown"));
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack current;
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        if (VanishManager.isVanished(player) && (current = event.getCurrentItem()) != null && (this.plugin.getSpectatorItemManager().isPlayerSelectorItem(current) || this.plugin.getSpectatorItemManager().isLeaveSpectateItem(current))) {
            event.setCancelled(true);
        }
    }
}

