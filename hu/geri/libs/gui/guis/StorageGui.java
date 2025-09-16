/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.inventory.ItemStack
 */
package hu.geri.libs.gui.guis;

import hu.geri.libs.gui.components.InteractionModifier;
import hu.geri.libs.gui.guis.BaseGui;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class StorageGui
extends BaseGui {
    public StorageGui(int rows, @NotNull String title, @NotNull Set<InteractionModifier> interactionModifiers) {
        super(rows, title, interactionModifiers);
    }

    @Deprecated
    public StorageGui(int rows, @NotNull String title) {
        super(rows, title);
    }

    @Deprecated
    public StorageGui(@NotNull String title) {
        super(1, title);
    }

    @NotNull
    public @NotNull Map<@NotNull Integer, @NotNull ItemStack> addItem(@NotNull ItemStack ... items) {
        return Collections.unmodifiableMap(this.getInventory().addItem(items));
    }

    public Map<@NotNull Integer, @NotNull ItemStack> addItem(@NotNull List<ItemStack> items) {
        return this.addItem(items.toArray(new ItemStack[0]));
    }

    @Override
    public void open(@NotNull HumanEntity player) {
        if (player.isSleeping()) {
            return;
        }
        this.populateGui();
        player.openInventory(this.getInventory());
    }
}

