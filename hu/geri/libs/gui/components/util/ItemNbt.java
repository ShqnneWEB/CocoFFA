/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package hu.geri.libs.gui.components.util;

import hu.geri.libs.gui.components.nbt.LegacyNbt;
import hu.geri.libs.gui.components.nbt.NbtWrapper;
import hu.geri.libs.gui.components.nbt.Pdc;
import hu.geri.libs.gui.components.util.VersionHelper;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class ItemNbt {
    private static final NbtWrapper nbt = ItemNbt.selectNbt();

    public static ItemStack setString(@NotNull ItemStack itemStack, @NotNull String key, @NotNull String value) {
        return nbt.setString(itemStack, key, value);
    }

    public static String getString(@NotNull ItemStack itemStack, @NotNull String key) {
        return nbt.getString(itemStack, key);
    }

    public static ItemStack setBoolean(@NotNull ItemStack itemStack, @NotNull String key, boolean value) {
        return nbt.setBoolean(itemStack, key, value);
    }

    public static ItemStack removeTag(@NotNull ItemStack itemStack, @NotNull String key) {
        return nbt.removeTag(itemStack, key);
    }

    private static NbtWrapper selectNbt() {
        if (VersionHelper.IS_PDC_VERSION) {
            return new Pdc();
        }
        return new LegacyNbt();
    }
}

