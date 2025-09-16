/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.inventory.PrepareItemCraftEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.persistence.PersistentDataType
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.listeners;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import java.util.List;
import java.util.WeakHashMap;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class StarterItemListener
implements Listener {
    private final CocoFFA plugin;
    private final WeakHashMap<Player, Long> cooldown = new WeakHashMap();
    private final NamespacedKey starterKey;

    public StarterItemListener(CocoFFA plugin) {
        this.plugin = plugin;
        this.starterKey = new NamespacedKey((Plugin)plugin, "cocoffa-starter");
    }

    @EventHandler
    private void onInteract(@NotNull PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        if (item.getType().equals((Object)Material.AIR)) {
            return;
        }
        if (!event.getAction().equals((Object)Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals((Object)Action.RIGHT_CLICK_AIR)) {
            return;
        }
        if (!item.hasItemMeta() || item.getItemMeta() == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(this.starterKey, PersistentDataType.STRING)) {
            return;
        }
        String arenaName = (String)meta.getPersistentDataContainer().get(this.starterKey, PersistentDataType.STRING);
        if (arenaName == null) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (this.cooldown.containsKey(player) && System.currentTimeMillis() - this.cooldown.get(player) < 300L) {
            return;
        }
        this.cooldown.put(player, System.currentTimeMillis());
        if (!this.plugin.getArenaManager().getArenaNames().contains(arenaName)) {
            return;
        }
        Arena arena = this.plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            return;
        }
        if (!arena.isStarterEnabled()) {
            return;
        }
        if (arena.isRunning()) {
            String message = this.plugin.getLocaleManager().getMessage("starter.already-running", "{arena_name}", arenaName, "{player}", player.getName());
            player.sendMessage(message);
            return;
        }
        if (!arena.isConfigured()) {
            List<String> missingSettings = arena.getMissingSettings();
            this.plugin.getServer().getConsoleSender().sendMessage(this.plugin.getLocaleManager().getMessage("console.arena.missing-settings.header"));
            for (String missing : missingSettings) {
                this.plugin.getServer().getConsoleSender().sendMessage(this.plugin.getLocaleManager().getMessage("console.arena.missing-settings.item", "{missing}", missing));
            }
            this.plugin.getServer().getConsoleSender().sendMessage(this.plugin.getLocaleManager().getMessage("console.arena.missing-settings.footer-with-command", "{command}", this.plugin.getConfigManager().getMainCommand()));
            player.sendMessage(this.plugin.getLocaleManager().getMessage("commands.start.missing-config", "{arena_name}", arenaName));
            return;
        }
        item.setAmount(item.getAmount() - 1);
        this.plugin.getArenaManager().startArena(arena);
        String successMessage = this.plugin.getLocaleManager().getMessage("starter.start", "{arena_name}", arenaName, "{player}", player.getName());
        player.sendMessage(successMessage);
        String broadcastMessage = this.plugin.getLocaleManager().getMessage("arena-started-broadcast", "{player}", player.getName(), "{arena_name}", arenaName);
        this.plugin.getServer().broadcastMessage(broadcastMessage);
    }

    @EventHandler(ignoreCancelled=true)
    public void onCraft(@NotNull PrepareItemCraftEvent event) {
        for (ItemStack item : event.getInventory().getMatrix()) {
            ItemMeta meta;
            if (item == null || item.getType() == Material.AIR || !item.hasItemMeta() || item.getItemMeta() == null || !(meta = item.getItemMeta()).getPersistentDataContainer().has(this.starterKey, PersistentDataType.STRING)) continue;
            event.getInventory().setResult(null);
            return;
        }
    }

    public NamespacedKey getStarterKey() {
        return this.starterKey;
    }
}

