/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.event.inventory.InventoryOpenEvent
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.libs.gui.guis;

import hu.geri.libs.gui.TriumphGui;
import hu.geri.libs.gui.components.GuiAction;
import hu.geri.libs.gui.components.GuiType;
import hu.geri.libs.gui.components.InteractionModifier;
import hu.geri.libs.gui.components.exception.GuiException;
import hu.geri.libs.gui.components.util.GuiFiller;
import hu.geri.libs.gui.components.util.Legacy;
import hu.geri.libs.gui.components.util.VersionHelper;
import hu.geri.libs.gui.guis.GuiItem;
import hu.geri.libs.gui.guis.GuiListener;
import hu.geri.libs.gui.guis.InteractionModifierListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseGui
implements InventoryHolder {
    private static final Plugin plugin = TriumphGui.getPlugin();
    private static Method GET_SCHEDULER_METHOD = null;
    private static Method EXECUTE_METHOD = null;
    private final GuiFiller filler = new GuiFiller(this);
    private final Map<Integer, GuiItem> guiItems;
    private final Map<Integer, GuiAction<InventoryClickEvent>> slotActions;
    private final Set<InteractionModifier> interactionModifiers;
    private Inventory inventory;
    private String title;
    private int rows = 1;
    private GuiType guiType = GuiType.CHEST;
    private GuiAction<InventoryClickEvent> defaultClickAction;
    private GuiAction<InventoryClickEvent> defaultTopClickAction;
    private GuiAction<InventoryClickEvent> playerInventoryAction;
    private GuiAction<InventoryDragEvent> dragAction;
    private GuiAction<InventoryCloseEvent> closeGuiAction;
    private GuiAction<InventoryOpenEvent> openGuiAction;
    private GuiAction<InventoryClickEvent> outsideClickAction;
    private boolean updating;
    private boolean runCloseAction = true;
    private boolean runOpenAction = true;

    public BaseGui(int rows, @NotNull String title, @NotNull Set<InteractionModifier> interactionModifiers) {
        int finalRows = rows;
        if (rows < 1 || rows > 6) {
            finalRows = 1;
        }
        this.rows = finalRows;
        this.interactionModifiers = this.safeCopyOf(interactionModifiers);
        this.title = title;
        int inventorySize = this.rows * 9;
        this.inventory = Bukkit.createInventory((InventoryHolder)this, (int)inventorySize, (String)title);
        this.slotActions = new LinkedHashMap<Integer, GuiAction<InventoryClickEvent>>(inventorySize);
        this.guiItems = new LinkedHashMap<Integer, GuiItem>(inventorySize);
    }

    public BaseGui(@NotNull GuiType guiType, @NotNull String title, @NotNull Set<InteractionModifier> interactionModifiers) {
        this.guiType = guiType;
        this.interactionModifiers = this.safeCopyOf(interactionModifiers);
        this.title = title;
        int inventorySize = guiType.getLimit();
        this.inventory = Bukkit.createInventory((InventoryHolder)this, (InventoryType)guiType.getInventoryType(), (String)title);
        this.slotActions = new LinkedHashMap<Integer, GuiAction<InventoryClickEvent>>(inventorySize);
        this.guiItems = new LinkedHashMap<Integer, GuiItem>(inventorySize);
    }

    @Deprecated
    public BaseGui(int rows, @NotNull String title) {
        int finalRows = rows;
        if (rows < 1 || rows > 6) {
            finalRows = 1;
        }
        this.rows = finalRows;
        this.interactionModifiers = EnumSet.noneOf(InteractionModifier.class);
        this.title = title;
        this.inventory = Bukkit.createInventory((InventoryHolder)this, (int)(this.rows * 9), (String)title);
        this.slotActions = new LinkedHashMap<Integer, GuiAction<InventoryClickEvent>>();
        this.guiItems = new LinkedHashMap<Integer, GuiItem>();
    }

    @Deprecated
    public BaseGui(@NotNull GuiType guiType, @NotNull String title) {
        this.guiType = guiType;
        this.interactionModifiers = EnumSet.noneOf(InteractionModifier.class);
        this.title = title;
        this.inventory = Bukkit.createInventory((InventoryHolder)this, (InventoryType)this.guiType.getInventoryType(), (String)title);
        this.slotActions = new LinkedHashMap<Integer, GuiAction<InventoryClickEvent>>();
        this.guiItems = new LinkedHashMap<Integer, GuiItem>();
    }

    @NotNull
    private Set<InteractionModifier> safeCopyOf(@NotNull Set<InteractionModifier> set) {
        if (set.isEmpty()) {
            return EnumSet.noneOf(InteractionModifier.class);
        }
        return EnumSet.copyOf(set);
    }

    @Deprecated
    @NotNull
    public String getTitle() {
        return this.title;
    }

    @NotNull
    public Component title() {
        return Legacy.SERIALIZER.deserialize(this.title);
    }

    public void setItem(int slot, @NotNull GuiItem guiItem) {
        this.validateSlot(slot);
        this.guiItems.put(slot, guiItem);
    }

    public void removeItem(@NotNull GuiItem item) {
        Optional<Map.Entry> entry = this.guiItems.entrySet().stream().filter(it -> ((GuiItem)it.getValue()).equals(item)).findFirst();
        entry.ifPresent(it -> {
            this.guiItems.remove(it.getKey());
            this.inventory.remove(((GuiItem)it.getValue()).getItemStack());
        });
    }

    public void removeItem(@NotNull ItemStack item) {
        Optional<Map.Entry> entry = this.guiItems.entrySet().stream().filter(it -> ((GuiItem)it.getValue()).getItemStack().equals((Object)item)).findFirst();
        entry.ifPresent(it -> {
            this.guiItems.remove(it.getKey());
            this.inventory.remove(item);
        });
    }

    public void removeItem(int slot) {
        this.validateSlot(slot);
        this.guiItems.remove(slot);
        this.inventory.setItem(slot, null);
    }

    public void removeItem(int row, int col) {
        this.removeItem(this.getSlotFromRowCol(row, col));
    }

    public void setItem(@NotNull List<Integer> slots, @NotNull GuiItem guiItem) {
        for (int slot : slots) {
            this.setItem(slot, guiItem);
        }
    }

    public void setItem(int row, int col, @NotNull GuiItem guiItem) {
        this.setItem(this.getSlotFromRowCol(row, col), guiItem);
    }

    public void addItem(@NotNull GuiItem ... items) {
        this.addItem(false, items);
    }

    public void addItem(boolean expandIfFull, @NotNull GuiItem ... items) {
        ArrayList<GuiItem> notAddedItems = new ArrayList<GuiItem>();
        block0: for (GuiItem guiItem : items) {
            for (int slot = 0; slot < this.rows * 9; ++slot) {
                if (this.guiItems.get(slot) != null) {
                    if (slot != this.rows * 9 - 1) continue;
                    notAddedItems.add(guiItem);
                    continue;
                }
                this.guiItems.put(slot, guiItem);
                continue block0;
            }
        }
        if (!expandIfFull || this.rows >= 6 || notAddedItems.isEmpty() || this.guiType != null && this.guiType != GuiType.CHEST) {
            return;
        }
        ++this.rows;
        this.inventory = Bukkit.createInventory((InventoryHolder)this, (int)(this.rows * 9), (String)this.title);
        this.update();
        this.addItem(true, notAddedItems.toArray(new GuiItem[0]));
    }

    public void addSlotAction(int slot, @Nullable GuiAction<@NotNull InventoryClickEvent> slotAction) {
        this.validateSlot(slot);
        this.slotActions.put(slot, slotAction);
    }

    public void addSlotAction(int row, int col, @Nullable GuiAction<@NotNull InventoryClickEvent> slotAction) {
        this.addSlotAction(this.getSlotFromRowCol(row, col), slotAction);
    }

    @Nullable
    public GuiItem getGuiItem(int slot) {
        return this.guiItems.get(slot);
    }

    public boolean isUpdating() {
        return this.updating;
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
    }

    public void open(@NotNull HumanEntity player) {
        if (player.isSleeping()) {
            return;
        }
        this.inventory.clear();
        this.populateGui();
        player.openInventory(this.inventory);
    }

    public void close(@NotNull HumanEntity player) {
        this.close(player, true);
    }

    public void close(@NotNull HumanEntity player, boolean runCloseAction) {
        Runnable task = () -> {
            this.runCloseAction = runCloseAction;
            player.closeInventory();
            this.runCloseAction = true;
        };
        if (VersionHelper.IS_FOLIA) {
            if (GET_SCHEDULER_METHOD == null || EXECUTE_METHOD == null) {
                throw new GuiException("Could not find Folia Scheduler methods.");
            }
            try {
                EXECUTE_METHOD.invoke(GET_SCHEDULER_METHOD.invoke(player, new Object[0]), plugin, task, null, 2L);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new GuiException("Could not invoke Folia task.", e);
            }
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, task, 2L);
    }

    public void update() {
        this.inventory.clear();
        this.populateGui();
        for (HumanEntity viewer : new ArrayList(this.inventory.getViewers())) {
            ((Player)viewer).updateInventory();
        }
    }

    @Contract(value="_ -> this")
    @NotNull
    public BaseGui updateTitle(@NotNull String title) {
        this.updating = true;
        ArrayList viewers = new ArrayList(this.inventory.getViewers());
        this.inventory = Bukkit.createInventory((InventoryHolder)this, (int)this.inventory.getSize(), (String)title);
        for (HumanEntity player : viewers) {
            this.open(player);
        }
        this.updating = false;
        this.title = title;
        return this;
    }

    public void updateItem(int slot, @NotNull ItemStack itemStack) {
        GuiItem guiItem = this.guiItems.get(slot);
        if (guiItem == null) {
            this.updateItem(slot, new GuiItem(itemStack));
            return;
        }
        guiItem.setItemStack(itemStack);
        this.updateItem(slot, guiItem);
    }

    public void updateItem(int row, int col, @NotNull ItemStack itemStack) {
        this.updateItem(this.getSlotFromRowCol(row, col), itemStack);
    }

    public void updateItem(int slot, @NotNull GuiItem item) {
        this.guiItems.put(slot, item);
        this.inventory.setItem(slot, item.getItemStack());
    }

    public void updateItem(int row, int col, @NotNull GuiItem item) {
        this.updateItem(this.getSlotFromRowCol(row, col), item);
    }

    @NotNull
    @Contract(value=" -> this")
    public BaseGui disableItemPlace() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_PLACE);
        return this;
    }

    @NotNull
    @Contract(value=" -> this")
    public BaseGui disableItemTake() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_TAKE);
        return this;
    }

    @NotNull
    @Contract(value=" -> this")
    public BaseGui disableItemSwap() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_SWAP);
        return this;
    }

    @NotNull
    @Contract(value=" -> this")
    public BaseGui disableItemDrop() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_DROP);
        return this;
    }

    @NotNull
    @Contract(value=" -> this")
    public BaseGui disableOtherActions() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return this;
    }

    @NotNull
    @Contract(value=" -> this")
    public BaseGui disableAllInteractions() {
        this.interactionModifiers.addAll(InteractionModifier.VALUES);
        return this;
    }

    @NotNull
    @Contract(value=" -> this")
    public BaseGui enableItemPlace() {
        this.interactionModifiers.remove((Object)InteractionModifier.PREVENT_ITEM_PLACE);
        return this;
    }

    @NotNull
    @Contract(value=" -> this")
    public BaseGui enableItemTake() {
        this.interactionModifiers.remove((Object)InteractionModifier.PREVENT_ITEM_TAKE);
        return this;
    }

    @NotNull
    @Contract(value=" -> this")
    public BaseGui enableItemSwap() {
        this.interactionModifiers.remove((Object)InteractionModifier.PREVENT_ITEM_SWAP);
        return this;
    }

    @NotNull
    @Contract(value=" -> this")
    public BaseGui enableItemDrop() {
        this.interactionModifiers.remove((Object)InteractionModifier.PREVENT_ITEM_DROP);
        return this;
    }

    @NotNull
    @Contract(value=" -> this")
    public BaseGui enableOtherActions() {
        this.interactionModifiers.remove((Object)InteractionModifier.PREVENT_OTHER_ACTIONS);
        return this;
    }

    @NotNull
    @Contract(value=" -> this")
    public BaseGui enableAllInteractions() {
        this.interactionModifiers.clear();
        return this;
    }

    public boolean allInteractionsDisabled() {
        return this.interactionModifiers.size() == InteractionModifier.VALUES.size();
    }

    public boolean canPlaceItems() {
        return !this.interactionModifiers.contains((Object)InteractionModifier.PREVENT_ITEM_PLACE);
    }

    public boolean canTakeItems() {
        return !this.interactionModifiers.contains((Object)InteractionModifier.PREVENT_ITEM_TAKE);
    }

    public boolean canSwapItems() {
        return !this.interactionModifiers.contains((Object)InteractionModifier.PREVENT_ITEM_SWAP);
    }

    public boolean canDropItems() {
        return !this.interactionModifiers.contains((Object)InteractionModifier.PREVENT_ITEM_DROP);
    }

    public boolean allowsOtherActions() {
        return !this.interactionModifiers.contains((Object)InteractionModifier.PREVENT_OTHER_ACTIONS);
    }

    @NotNull
    public GuiFiller getFiller() {
        return this.filler;
    }

    @NotNull
    public @NotNull Map<@NotNull Integer, @NotNull GuiItem> getGuiItems() {
        return this.guiItems;
    }

    @NotNull
    public Inventory getInventory() {
        return this.inventory;
    }

    public void setInventory(@NotNull Inventory inventory) {
        this.inventory = inventory;
    }

    public int getRows() {
        return this.rows;
    }

    @NotNull
    public GuiType guiType() {
        return this.guiType;
    }

    @Nullable
    GuiAction<InventoryClickEvent> getDefaultClickAction() {
        return this.defaultClickAction;
    }

    public void setDefaultClickAction(@Nullable GuiAction<@NotNull InventoryClickEvent> defaultClickAction) {
        this.defaultClickAction = defaultClickAction;
    }

    @Nullable
    GuiAction<InventoryClickEvent> getDefaultTopClickAction() {
        return this.defaultTopClickAction;
    }

    public void setDefaultTopClickAction(@Nullable GuiAction<@NotNull InventoryClickEvent> defaultTopClickAction) {
        this.defaultTopClickAction = defaultTopClickAction;
    }

    @Nullable
    GuiAction<InventoryClickEvent> getPlayerInventoryAction() {
        return this.playerInventoryAction;
    }

    public void setPlayerInventoryAction(@Nullable GuiAction<@NotNull InventoryClickEvent> playerInventoryAction) {
        this.playerInventoryAction = playerInventoryAction;
    }

    @Nullable
    GuiAction<InventoryDragEvent> getDragAction() {
        return this.dragAction;
    }

    public void setDragAction(@Nullable GuiAction<@NotNull InventoryDragEvent> dragAction) {
        this.dragAction = dragAction;
    }

    @Nullable
    GuiAction<InventoryCloseEvent> getCloseGuiAction() {
        return this.closeGuiAction;
    }

    public void setCloseGuiAction(@Nullable GuiAction<@NotNull InventoryCloseEvent> closeGuiAction) {
        this.closeGuiAction = closeGuiAction;
    }

    @Nullable
    GuiAction<InventoryOpenEvent> getOpenGuiAction() {
        return this.openGuiAction;
    }

    public void setOpenGuiAction(@Nullable GuiAction<@NotNull InventoryOpenEvent> openGuiAction) {
        this.openGuiAction = openGuiAction;
    }

    @Nullable
    GuiAction<InventoryClickEvent> getOutsideClickAction() {
        return this.outsideClickAction;
    }

    public void setOutsideClickAction(@Nullable GuiAction<@NotNull InventoryClickEvent> outsideClickAction) {
        this.outsideClickAction = outsideClickAction;
    }

    @Nullable
    GuiAction<InventoryClickEvent> getSlotAction(int slot) {
        return this.slotActions.get(slot);
    }

    void populateGui() {
        for (Map.Entry<Integer, GuiItem> entry : this.guiItems.entrySet()) {
            this.inventory.setItem(entry.getKey().intValue(), entry.getValue().getItemStack());
        }
    }

    boolean shouldRunCloseAction() {
        return this.runCloseAction;
    }

    boolean shouldRunOpenAction() {
        return this.runOpenAction;
    }

    int getSlotFromRowCol(int row, int col) {
        return col + (row - 1) * 9 - 1;
    }

    private void validateSlot(int slot) {
        int limit = this.guiType.getLimit();
        if (this.guiType == GuiType.CHEST) {
            if (slot < 0 || slot >= this.rows * limit) {
                this.throwInvalidSlot(slot);
            }
            return;
        }
        if (slot < 0 || slot > limit) {
            this.throwInvalidSlot(slot);
        }
    }

    private void throwInvalidSlot(int slot) {
        if (this.guiType == GuiType.CHEST) {
            throw new GuiException("Slot " + slot + " is not valid for the gui type - " + this.guiType.name() + " and rows - " + this.rows + "!");
        }
        throw new GuiException("Slot " + slot + " is not valid for the gui type - " + this.guiType.name() + "!");
    }

    static {
        try {
            GET_SCHEDULER_METHOD = Entity.class.getMethod("getScheduler", new Class[0]);
            Class<?> entityScheduler = Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
            EXECUTE_METHOD = entityScheduler.getMethod("execute", Plugin.class, Runnable.class, Runnable.class, Long.TYPE);
        } catch (ClassNotFoundException | NoSuchMethodException reflectiveOperationException) {
            // empty catch block
        }
        Bukkit.getPluginManager().registerEvents((Listener)new GuiListener(), plugin);
        Bukkit.getPluginManager().registerEvents((Listener)new InteractionModifierListener(), plugin);
    }
}

