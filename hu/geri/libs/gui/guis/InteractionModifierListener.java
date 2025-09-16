/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryAction
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.Inventory
 */
package hu.geri.libs.gui.guis;

import com.google.common.base.Preconditions;
import hu.geri.libs.gui.guis.BaseGui;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public final class InteractionModifierListener
implements Listener {
    private static final Set<InventoryAction> ITEM_TAKE_ACTIONS = Collections.unmodifiableSet(EnumSet.of(InventoryAction.PICKUP_ONE, new InventoryAction[]{InventoryAction.PICKUP_SOME, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ALL, InventoryAction.COLLECT_TO_CURSOR, InventoryAction.HOTBAR_SWAP, InventoryAction.MOVE_TO_OTHER_INVENTORY}));
    private static final Set<InventoryAction> ITEM_PLACE_ACTIONS = Collections.unmodifiableSet(EnumSet.of(InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME, InventoryAction.PLACE_ALL));
    private static final Set<InventoryAction> ITEM_SWAP_ACTIONS = Collections.unmodifiableSet(EnumSet.of(InventoryAction.HOTBAR_SWAP, InventoryAction.SWAP_WITH_CURSOR, InventoryAction.HOTBAR_MOVE_AND_READD));
    private static final Set<InventoryAction> ITEM_DROP_ACTIONS = Collections.unmodifiableSet(EnumSet.of(InventoryAction.DROP_ONE_SLOT, InventoryAction.DROP_ALL_SLOT, InventoryAction.DROP_ONE_CURSOR, InventoryAction.DROP_ALL_CURSOR));

    @EventHandler
    public void onGuiClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui)) {
            return;
        }
        BaseGui gui = (BaseGui)event.getInventory().getHolder();
        if (gui.allInteractionsDisabled()) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return;
        }
        if (!gui.canPlaceItems() && this.isPlaceItemEvent(event) || !gui.canTakeItems() && this.isTakeItemEvent(event) || !gui.canSwapItems() && this.isSwapItemEvent(event) || !gui.canDropItems() && this.isDropItemEvent(event) || !gui.allowsOtherActions() && this.isOtherEvent(event)) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onGuiDrag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui)) {
            return;
        }
        BaseGui gui = (BaseGui)event.getInventory().getHolder();
        if (gui.allInteractionsDisabled()) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return;
        }
        if (gui.canPlaceItems() || !this.isDraggingOnGui(event)) {
            return;
        }
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
    }

    private boolean isTakeItemEvent(InventoryClickEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");
        Inventory inventory = event.getInventory();
        Inventory clickedInventory = event.getClickedInventory();
        InventoryAction action = event.getAction();
        if (clickedInventory != null && clickedInventory.getType() == InventoryType.PLAYER || inventory.getType() == InventoryType.PLAYER) {
            return false;
        }
        return action == InventoryAction.MOVE_TO_OTHER_INVENTORY || this.isTakeAction(action);
    }

    private boolean isPlaceItemEvent(InventoryClickEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");
        Inventory inventory = event.getInventory();
        Inventory clickedInventory = event.getClickedInventory();
        InventoryAction action = event.getAction();
        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY && clickedInventory != null && clickedInventory.getType() == InventoryType.PLAYER && inventory.getType() != clickedInventory.getType()) {
            return true;
        }
        return this.isPlaceAction(action) && (clickedInventory == null || clickedInventory.getType() != InventoryType.PLAYER) && inventory.getType() != InventoryType.PLAYER;
    }

    private boolean isSwapItemEvent(InventoryClickEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");
        Inventory inventory = event.getInventory();
        Inventory clickedInventory = event.getClickedInventory();
        InventoryAction action = event.getAction();
        return this.isSwapAction(action) && (clickedInventory == null || clickedInventory.getType() != InventoryType.PLAYER) && inventory.getType() != InventoryType.PLAYER;
    }

    private boolean isDropItemEvent(InventoryClickEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");
        Inventory inventory = event.getInventory();
        Inventory clickedInventory = event.getClickedInventory();
        InventoryAction action = event.getAction();
        return this.isDropAction(action) && (clickedInventory != null || inventory.getType() != InventoryType.PLAYER);
    }

    private boolean isOtherEvent(InventoryClickEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");
        Inventory inventory = event.getInventory();
        Inventory clickedInventory = event.getClickedInventory();
        InventoryAction action = event.getAction();
        return this.isOtherAction(action) && (clickedInventory != null || inventory.getType() != InventoryType.PLAYER);
    }

    private boolean isDraggingOnGui(InventoryDragEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");
        int topSlots = event.getView().getTopInventory().getSize();
        return event.getRawSlots().stream().anyMatch(slot -> slot < topSlots);
    }

    private boolean isTakeAction(InventoryAction action) {
        Preconditions.checkNotNull(action, "action cannot be null");
        return ITEM_TAKE_ACTIONS.contains(action);
    }

    private boolean isPlaceAction(InventoryAction action) {
        Preconditions.checkNotNull(action, "action cannot be null");
        return ITEM_PLACE_ACTIONS.contains(action);
    }

    private boolean isSwapAction(InventoryAction action) {
        Preconditions.checkNotNull(action, "action cannot be null");
        return ITEM_SWAP_ACTIONS.contains(action);
    }

    private boolean isDropAction(InventoryAction action) {
        Preconditions.checkNotNull(action, "action cannot be null");
        return ITEM_DROP_ACTIONS.contains(action);
    }

    private boolean isOtherAction(InventoryAction action) {
        Preconditions.checkNotNull(action, "action cannot be null");
        return action == InventoryAction.CLONE_STACK || action == InventoryAction.UNKNOWN;
    }
}

