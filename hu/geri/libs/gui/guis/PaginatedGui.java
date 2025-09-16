/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package hu.geri.libs.gui.guis;

import hu.geri.libs.gui.components.InteractionModifier;
import hu.geri.libs.gui.guis.BaseGui;
import hu.geri.libs.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PaginatedGui
extends BaseGui {
    private final List<GuiItem> pageItems = new ArrayList<GuiItem>();
    private final Map<Integer, GuiItem> currentPage;
    private int pageSize;
    private int pageNum = 1;

    public PaginatedGui(int rows, int pageSize, @NotNull String title, @NotNull Set<InteractionModifier> interactionModifiers) {
        super(rows, title, interactionModifiers);
        this.pageSize = pageSize;
        int inventorySize = rows * 9;
        this.currentPage = new LinkedHashMap<Integer, GuiItem>(inventorySize);
    }

    @Deprecated
    public PaginatedGui(int rows, int pageSize, @NotNull String title) {
        super(rows, title);
        this.pageSize = pageSize;
        int inventorySize = rows * 9;
        this.currentPage = new LinkedHashMap<Integer, GuiItem>(inventorySize);
    }

    @Deprecated
    public PaginatedGui(int rows, @NotNull String title) {
        this(rows, 0, title);
    }

    @Deprecated
    public PaginatedGui(@NotNull String title) {
        this(2, title);
    }

    public BaseGui setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public void addItem(@NotNull GuiItem item) {
        this.pageItems.add(item);
    }

    @Override
    public void addItem(@NotNull GuiItem ... items) {
        this.pageItems.addAll(Arrays.asList(items));
    }

    @Override
    public void update() {
        this.getInventory().clear();
        this.populateGui();
        this.updatePage();
    }

    public void updatePageItem(int slot, @NotNull ItemStack itemStack) {
        if (!this.currentPage.containsKey(slot)) {
            return;
        }
        GuiItem guiItem = this.currentPage.get(slot);
        guiItem.setItemStack(itemStack);
        this.getInventory().setItem(slot, guiItem.getItemStack());
    }

    public void updatePageItem(int row, int col, @NotNull ItemStack itemStack) {
        this.updateItem(this.getSlotFromRowCol(row, col), itemStack);
    }

    public void updatePageItem(int slot, @NotNull GuiItem item) {
        if (!this.currentPage.containsKey(slot)) {
            return;
        }
        GuiItem oldItem = this.currentPage.get(slot);
        int index = this.pageItems.indexOf(this.currentPage.get(slot));
        this.currentPage.put(slot, item);
        this.pageItems.set(index, item);
        this.getInventory().setItem(slot, item.getItemStack());
    }

    public void updatePageItem(int row, int col, @NotNull GuiItem item) {
        this.updateItem(this.getSlotFromRowCol(row, col), item);
    }

    public void removePageItem(@NotNull GuiItem item) {
        this.pageItems.remove(item);
        this.updatePage();
    }

    public void removePageItem(@NotNull ItemStack item) {
        Optional<GuiItem> guiItem = this.pageItems.stream().filter(it -> it.getItemStack().equals((Object)item)).findFirst();
        guiItem.ifPresent(this::removePageItem);
    }

    @Override
    public void open(@NotNull HumanEntity player) {
        this.open(player, 1);
    }

    public void open(@NotNull HumanEntity player, int openPage) {
        if (player.isSleeping()) {
            return;
        }
        if (openPage <= this.getPagesNum() || openPage > 0) {
            this.pageNum = openPage;
        }
        this.getInventory().clear();
        this.currentPage.clear();
        this.populateGui();
        if (this.pageSize == 0) {
            this.pageSize = this.calculatePageSize();
        }
        this.populatePage();
        player.openInventory(this.getInventory());
    }

    @Override
    @NotNull
    public BaseGui updateTitle(@NotNull String title) {
        this.setUpdating(true);
        ArrayList viewers = new ArrayList(this.getInventory().getViewers());
        this.setInventory(Bukkit.createInventory((InventoryHolder)this, (int)this.getInventory().getSize(), (String)title));
        for (HumanEntity player : viewers) {
            this.open(player, this.getPageNum());
        }
        this.setUpdating(false);
        return this;
    }

    @NotNull
    public @NotNull Map<@NotNull Integer, @NotNull GuiItem> getCurrentPageItems() {
        return Collections.unmodifiableMap(this.currentPage);
    }

    @NotNull
    public @NotNull List<@NotNull GuiItem> getPageItems() {
        return Collections.unmodifiableList(this.pageItems);
    }

    public int getCurrentPageNum() {
        return this.pageNum;
    }

    public int getNextPageNum() {
        if (this.pageNum + 1 > this.getPagesNum()) {
            return this.pageNum;
        }
        return this.pageNum + 1;
    }

    public int getPrevPageNum() {
        if (this.pageNum - 1 == 0) {
            return this.pageNum;
        }
        return this.pageNum - 1;
    }

    public boolean next() {
        if (this.pageNum + 1 > this.getPagesNum()) {
            return false;
        }
        ++this.pageNum;
        this.updatePage();
        return true;
    }

    public boolean previous() {
        if (this.pageNum - 1 == 0) {
            return false;
        }
        --this.pageNum;
        this.updatePage();
        return true;
    }

    GuiItem getPageItem(int slot) {
        return this.currentPage.get(slot);
    }

    private List<GuiItem> getPageNum(int givenPage) {
        int page = givenPage - 1;
        ArrayList<GuiItem> guiPage = new ArrayList<GuiItem>();
        int max = page * this.pageSize + this.pageSize;
        if (max > this.pageItems.size()) {
            max = this.pageItems.size();
        }
        for (int i = page * this.pageSize; i < max; ++i) {
            guiPage.add(this.pageItems.get(i));
        }
        return guiPage;
    }

    public int getPagesNum() {
        if (this.pageSize == 0) {
            this.pageSize = this.calculatePageSize();
        }
        return (int)Math.ceil((double)this.pageItems.size() / (double)this.pageSize);
    }

    private void populatePage() {
        int slot = 0;
        int inventorySize = this.getInventory().getSize();
        Iterator<GuiItem> iterator = this.getPageNum(this.pageNum).iterator();
        while (iterator.hasNext() && slot < inventorySize) {
            if (this.getGuiItem(slot) != null || this.getInventory().getItem(slot) != null) {
                ++slot;
                continue;
            }
            GuiItem guiItem = iterator.next();
            this.currentPage.put(slot, guiItem);
            this.getInventory().setItem(slot, guiItem.getItemStack());
            ++slot;
        }
    }

    Map<Integer, GuiItem> getMutableCurrentPageItems() {
        return this.currentPage;
    }

    void clearPage() {
        for (Map.Entry<Integer, GuiItem> entry : this.currentPage.entrySet()) {
            this.getInventory().setItem(entry.getKey().intValue(), null);
        }
    }

    public void clearPageItems(boolean update) {
        this.pageItems.clear();
        if (update) {
            this.update();
        }
    }

    public void clearPageItems() {
        this.clearPageItems(false);
    }

    int getPageSize() {
        return this.pageSize;
    }

    int getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    void updatePage() {
        this.clearPage();
        this.populatePage();
    }

    int calculatePageSize() {
        int counter = 0;
        for (int slot = 0; slot < this.getRows() * 9; ++slot) {
            if (this.getGuiItem(slot) != null) continue;
            ++counter;
        }
        if (counter == 0) {
            return 1;
        }
        return counter;
    }
}

