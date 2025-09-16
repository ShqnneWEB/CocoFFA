/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.NamespacedKey
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.persistence.PersistentDataType
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package hu.geri.libs.gui.components.nbt;

import hu.geri.libs.gui.components.nbt.NbtWrapper;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Pdc
implements NbtWrapper {
    private static final Plugin PLUGIN = JavaPlugin.getProvidingPlugin(Pdc.class);

    @Override
    public ItemStack setString(@NotNull ItemStack itemStack, String key, String value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return itemStack;
        }
        meta.getPersistentDataContainer().set(new NamespacedKey(PLUGIN, key), PersistentDataType.STRING, (Object)value);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public ItemStack removeTag(@NotNull ItemStack itemStack, String key) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return itemStack;
        }
        meta.getPersistentDataContainer().remove(new NamespacedKey(PLUGIN, key));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public ItemStack setBoolean(@NotNull ItemStack itemStack, String key, boolean value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return itemStack;
        }
        meta.getPersistentDataContainer().set(new NamespacedKey(PLUGIN, key), PersistentDataType.BYTE, (Object)(value ? (byte)1 : 0));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    @Nullable
    public String getString(@NotNull ItemStack itemStack, String key) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return null;
        }
        return (String)meta.getPersistentDataContainer().get(new NamespacedKey(PLUGIN, key), PersistentDataType.STRING);
    }
}

