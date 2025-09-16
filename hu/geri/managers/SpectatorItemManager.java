/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.persistence.PersistentDataType
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.managers;

import hu.geri.CocoFFA;
import hu.geri.processor.MessageProcessor;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class SpectatorItemManager {
    private final CocoFFA plugin;
    private final NamespacedKey playerSelectorKey;
    private final NamespacedKey leaveSpectateKey;

    public SpectatorItemManager(CocoFFA plugin) {
        this.plugin = plugin;
        this.playerSelectorKey = new NamespacedKey((Plugin)plugin, "cocoffa-player-selector");
        this.leaveSpectateKey = new NamespacedKey((Plugin)plugin, "cocoffa-leave-spectate");
    }

    public ItemStack createPlayerSelectorItem() {
        Material material;
        String materialName = this.plugin.getConfigManager().getPlayerSelectorMaterial();
        try {
            material = Material.valueOf((String)materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.COMPASS;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String displayName = this.plugin.getConfigManager().getPlayerSelectorName();
            meta.setDisplayName(MessageProcessor.process(displayName));
            List<String> lore = this.plugin.getConfigManager().getPlayerSelectorLore();
            if (lore != null && !lore.isEmpty()) {
                List processedLore = lore.stream().map(MessageProcessor::process).collect(Collectors.toList());
                meta.setLore(processedLore);
            }
            meta.getPersistentDataContainer().set(this.playerSelectorKey, PersistentDataType.STRING, (Object)"player-selector");
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack createLeaveSpectateItem() {
        Material material;
        String materialName = this.plugin.getConfigManager().getLeaveSpectateMaterial();
        try {
            material = Material.valueOf((String)materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.BARRIER;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String displayName = this.plugin.getConfigManager().getLeaveSpectateName();
            meta.setDisplayName(MessageProcessor.process(displayName));
            List<String> lore = this.plugin.getConfigManager().getLeaveSpectateLore();
            if (lore != null && !lore.isEmpty()) {
                List processedLore = lore.stream().map(MessageProcessor::process).collect(Collectors.toList());
                meta.setLore(processedLore);
            }
            meta.getPersistentDataContainer().set(this.leaveSpectateKey, PersistentDataType.STRING, (Object)"leave-spectate");
            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean isPlayerSelectorItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(this.playerSelectorKey, PersistentDataType.STRING);
    }

    public boolean isLeaveSpectateItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(this.leaveSpectateKey, PersistentDataType.STRING);
    }
}

