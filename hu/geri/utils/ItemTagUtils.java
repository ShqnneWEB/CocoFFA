/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.NamespacedKey
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.persistence.PersistentDataType
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ItemTagUtils {
    private static final String OFFHAND_TAG_KEY = "cocoffa_offhand";
    private static final String AUTO_ARMOR_TAG_KEY = "cocoffa_auto_armor";

    public static ItemStack setOffhandTag(@NotNull Plugin plugin, @NotNull ItemStack item, boolean offhand) {
        ItemStack clonedItem = item.clone();
        ItemMeta meta = clonedItem.getItemMeta();
        if (meta != null) {
            NamespacedKey key = new NamespacedKey(plugin, OFFHAND_TAG_KEY);
            if (offhand) {
                meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (Object)1);
            } else {
                meta.getPersistentDataContainer().remove(key);
            }
            clonedItem.setItemMeta(meta);
        }
        return clonedItem;
    }

    public static boolean hasOffhandTag(@NotNull Plugin plugin, @NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        NamespacedKey key = new NamespacedKey(plugin, OFFHAND_TAG_KEY);
        return meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }

    public static ItemStack removeOffhandTag(@NotNull Plugin plugin, @NotNull ItemStack item) {
        ItemStack clonedItem = item.clone();
        ItemMeta meta = clonedItem.getItemMeta();
        if (meta != null) {
            NamespacedKey key = new NamespacedKey(plugin, OFFHAND_TAG_KEY);
            meta.getPersistentDataContainer().remove(key);
            clonedItem.setItemMeta(meta);
        }
        return clonedItem;
    }

    public static ItemStack setAutoArmorTag(@NotNull Plugin plugin, @NotNull ItemStack item, boolean autoArmor) {
        ItemStack clonedItem = item.clone();
        ItemMeta meta = clonedItem.getItemMeta();
        if (meta != null) {
            NamespacedKey key = new NamespacedKey(plugin, AUTO_ARMOR_TAG_KEY);
            if (autoArmor) {
                meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (Object)1);
            } else {
                meta.getPersistentDataContainer().remove(key);
            }
            clonedItem.setItemMeta(meta);
        }
        return clonedItem;
    }

    public static boolean hasAutoArmorTag(@NotNull Plugin plugin, @NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        NamespacedKey key = new NamespacedKey(plugin, AUTO_ARMOR_TAG_KEY);
        return meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }

    public static ItemStack removeAutoArmorTag(@NotNull Plugin plugin, @NotNull ItemStack item) {
        ItemStack clonedItem = item.clone();
        ItemMeta meta = clonedItem.getItemMeta();
        if (meta != null) {
            NamespacedKey key = new NamespacedKey(plugin, AUTO_ARMOR_TAG_KEY);
            meta.getPersistentDataContainer().remove(key);
            clonedItem.setItemMeta(meta);
        }
        return clonedItem;
    }
}

