/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.event.inventory.InventoryOpenEvent
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.ItemStack
 */
package hu.geri.libs.gui.guis;

import hu.geri.libs.gui.components.GuiAction;
import hu.geri.libs.gui.components.util.ItemNbt;
import hu.geri.libs.gui.guis.BaseGui;
import hu.geri.libs.gui.guis.GuiItem;
import hu.geri.libs.gui.guis.PaginatedGui;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class GuiListener
implements Listener {
    @EventHandler
    public void onGuiClick(InventoryClickEvent event) {
        GuiItem guiItem;
        GuiAction<InventoryClickEvent> slotAction;
        GuiAction<InventoryClickEvent> defaultClick;
        GuiAction<InventoryClickEvent> playerInventoryClick;
        if (!(event.getInventory().getHolder() instanceof BaseGui)) {
            return;
        }
        BaseGui gui = (BaseGui)event.getInventory().getHolder();
        GuiAction<InventoryClickEvent> outsideClickAction = gui.getOutsideClickAction();
        if (outsideClickAction != null && event.getClickedInventory() == null) {
            outsideClickAction.execute(event);
            return;
        }
        if (event.getClickedInventory() == null) {
            return;
        }
        GuiAction<InventoryClickEvent> defaultTopClick = gui.getDefaultTopClickAction();
        if (defaultTopClick != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
            defaultTopClick.execute(event);
        }
        if ((playerInventoryClick = gui.getPlayerInventoryAction()) != null && event.getClickedInventory().getType() == InventoryType.PLAYER) {
            playerInventoryClick.execute(event);
        }
        if ((defaultClick = gui.getDefaultClickAction()) != null) {
            defaultClick.execute(event);
        }
        if ((slotAction = gui.getSlotAction(event.getSlot())) != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
            slotAction.execute(event);
        }
        if (gui instanceof PaginatedGui) {
            PaginatedGui paginatedGui = (PaginatedGui)gui;
            guiItem = paginatedGui.getGuiItem(event.getSlot());
            if (guiItem == null) {
                guiItem = paginatedGui.getPageItem(event.getSlot());
            }
        } else {
            guiItem = gui.getGuiItem(event.getSlot());
        }
        if (!this.isGuiItem(event.getCurrentItem(), guiItem)) {
            return;
        }
        GuiAction<InventoryClickEvent> itemAction = guiItem.getAction();
        if (itemAction != null) {
            itemAction.execute(event);
        }
    }

    @EventHandler
    public void onGuiDrag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui)) {
            return;
        }
        BaseGui gui = (BaseGui)event.getInventory().getHolder();
        GuiAction<InventoryDragEvent> dragAction = gui.getDragAction();
        if (dragAction != null) {
            dragAction.execute(event);
        }
    }

    @EventHandler
    public void onGuiClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui)) {
            return;
        }
        BaseGui gui = (BaseGui)event.getInventory().getHolder();
        GuiAction<InventoryCloseEvent> closeAction = gui.getCloseGuiAction();
        if (closeAction != null && !gui.isUpdating() && gui.shouldRunCloseAction()) {
            closeAction.execute(event);
        }
    }

    @EventHandler
    public void onGuiOpen(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui)) {
            return;
        }
        BaseGui gui = (BaseGui)event.getInventory().getHolder();
        GuiAction<InventoryOpenEvent> openAction = gui.getOpenGuiAction();
        if (openAction != null && !gui.isUpdating()) {
            openAction.execute(event);
        }
    }

    private boolean isGuiItem(@Nullable ItemStack currentItem, @Nullable GuiItem guiItem) {
        if (currentItem == null || guiItem == null) {
            return false;
        }
        String nbt = ItemNbt.getString(currentItem, "mf-gui");
        if (nbt == null) {
            return false;
        }
        return nbt.equals(guiItem.getUuid().toString());
    }
}

