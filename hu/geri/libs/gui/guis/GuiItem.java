/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 */
package hu.geri.libs.gui.guis;

import com.google.common.base.Preconditions;
import hu.geri.libs.gui.components.GuiAction;
import hu.geri.libs.gui.components.util.ItemNbt;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuiItem {
    private final UUID uuid = UUID.randomUUID();
    private GuiAction<InventoryClickEvent> action;
    private ItemStack itemStack;

    public GuiItem(@NotNull ItemStack itemStack, @Nullable GuiAction<@NotNull InventoryClickEvent> action) {
        Preconditions.checkNotNull(itemStack, "The ItemStack for the GUI Item cannot be null!");
        this.action = action;
        this.setItemStack(itemStack);
    }

    public GuiItem(@NotNull ItemStack itemStack) {
        this(itemStack, null);
    }

    public GuiItem(@NotNull Material material) {
        this(new ItemStack(material), null);
    }

    public GuiItem(@NotNull Material material, @Nullable GuiAction<@NotNull InventoryClickEvent> action) {
        this(new ItemStack(material), action);
    }

    @NotNull
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public void setItemStack(@NotNull ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack, "The ItemStack for the GUI Item cannot be null!");
        this.itemStack = itemStack.getType() != Material.AIR ? ItemNbt.setString(itemStack.clone(), "mf-gui", this.uuid.toString()) : itemStack.clone();
    }

    @NotNull
    UUID getUuid() {
        return this.uuid;
    }

    @Nullable
    GuiAction<InventoryClickEvent> getAction() {
        return this.action;
    }

    public void setAction(@Nullable GuiAction<@NotNull InventoryClickEvent> action) {
        this.action = action;
    }
}

