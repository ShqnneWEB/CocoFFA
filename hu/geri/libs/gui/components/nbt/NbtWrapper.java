/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package hu.geri.libs.gui.components.nbt;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NbtWrapper {
    public ItemStack setString(@NotNull ItemStack var1, String var2, String var3);

    public ItemStack removeTag(@NotNull ItemStack var1, String var2);

    public ItemStack setBoolean(@NotNull ItemStack var1, String var2, boolean var3);

    @Nullable
    public String getString(@NotNull ItemStack var1, String var2);
}

