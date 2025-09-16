/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 */
package hu.geri.libs.gui.guis;

import hu.geri.libs.gui.components.InteractionModifier;
import hu.geri.libs.gui.components.ScrollType;
import hu.geri.libs.gui.guis.GuiItem;
import hu.geri.libs.gui.guis.PaginatedGui;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

public class ScrollingGui
extends PaginatedGui {
    private final ScrollType scrollType;
    private int scrollSize = 0;

    public ScrollingGui(int rows, int pageSize, @NotNull String title, @NotNull ScrollType scrollType, @NotNull Set<InteractionModifier> interactionModifiers) {
        super(rows, pageSize, title, interactionModifiers);
        this.scrollType = scrollType;
    }

    @Deprecated
    public ScrollingGui(int rows, int pageSize, @NotNull String title, @NotNull ScrollType scrollType) {
        super(rows, pageSize, title);
        this.scrollType = scrollType;
    }

    @Deprecated
    public ScrollingGui(int rows, int pageSize, @NotNull String title) {
        this(rows, pageSize, title, ScrollType.VERTICAL);
    }

    @Deprecated
    public ScrollingGui(int rows, @NotNull String title) {
        this(rows, 0, title, ScrollType.VERTICAL);
    }

    @Deprecated
    public ScrollingGui(int rows, @NotNull String title, @NotNull ScrollType scrollType) {
        this(rows, 0, title, scrollType);
    }

    @Deprecated
    public ScrollingGui(@NotNull String title) {
        this(2, title);
    }

    @Deprecated
    public ScrollingGui(@NotNull String title, @NotNull ScrollType scrollType) {
        this(2, title, scrollType);
    }

    @Override
    public boolean next() {
        if (this.getPageNum() * this.scrollSize + this.getPageSize() >= this.getPageItems().size() + this.scrollSize) {
            return false;
        }
        this.setPageNum(this.getPageNum() + 1);
        this.updatePage();
        return true;
    }

    @Override
    public boolean previous() {
        if (this.getPageNum() - 1 == 0) {
            return false;
        }
        this.setPageNum(this.getPageNum() - 1);
        this.updatePage();
        return true;
    }

    @Override
    public void open(@NotNull HumanEntity player) {
        this.open(player, 1);
    }

    @Override
    public void open(@NotNull HumanEntity player, int openPage) {
        if (player.isSleeping()) {
            return;
        }
        this.getInventory().clear();
        this.getMutableCurrentPageItems().clear();
        this.populateGui();
        if (this.getPageSize() == 0) {
            this.setPageSize(this.calculatePageSize());
        }
        if (this.scrollSize == 0) {
            this.scrollSize = this.calculateScrollSize();
        }
        if (openPage > 0 && openPage * this.scrollSize + this.getPageSize() <= this.getPageItems().size() + this.scrollSize) {
            this.setPageNum(openPage);
        }
        this.populatePage();
        player.openInventory(this.getInventory());
    }

    @Override
    void updatePage() {
        this.clearPage();
        this.populatePage();
    }

    private void populatePage() {
        for (GuiItem guiItem : this.getPage(this.getPageNum())) {
            if (this.scrollType == ScrollType.HORIZONTAL) {
                this.putItemHorizontally(guiItem);
                continue;
            }
            this.putItemVertically(guiItem);
        }
    }

    private int calculateScrollSize() {
        int counter = 0;
        if (this.scrollType == ScrollType.VERTICAL) {
            boolean foundCol = false;
            for (int row = 1; row <= this.getRows(); ++row) {
                for (int col = 1; col <= 9; ++col) {
                    int slot = this.getSlotFromRowCol(row, col);
                    if (this.getInventory().getItem(slot) != null) continue;
                    if (!foundCol) {
                        foundCol = true;
                    }
                    ++counter;
                }
                if (!foundCol) continue;
                return counter;
            }
            return counter;
        }
        boolean foundRow = false;
        for (int col = 1; col <= 9; ++col) {
            for (int row = 1; row <= this.getRows(); ++row) {
                int slot = this.getSlotFromRowCol(row, col);
                if (this.getInventory().getItem(slot) != null) continue;
                if (!foundRow) {
                    foundRow = true;
                }
                ++counter;
            }
            if (!foundRow) continue;
            return counter;
        }
        return counter;
    }

    private void putItemVertically(GuiItem guiItem) {
        for (int slot = 0; slot < this.getRows() * 9; ++slot) {
            if (this.getGuiItem(slot) != null || this.getInventory().getItem(slot) != null) continue;
            this.getMutableCurrentPageItems().put(slot, guiItem);
            this.getInventory().setItem(slot, guiItem.getItemStack());
            break;
        }
    }

    private void putItemHorizontally(GuiItem guiItem) {
        for (int col = 1; col < 10; ++col) {
            for (int row = 1; row <= this.getRows(); ++row) {
                int slot = this.getSlotFromRowCol(row, col);
                if (this.getGuiItem(slot) != null || this.getInventory().getItem(slot) != null) continue;
                this.getMutableCurrentPageItems().put(slot, guiItem);
                this.getInventory().setItem(slot, guiItem.getItemStack());
                return;
            }
        }
    }

    private List<GuiItem> getPage(int givenPage) {
        int page = givenPage - 1;
        int pageItemsSize = this.getPageItems().size();
        ArrayList<GuiItem> guiPage = new ArrayList<GuiItem>();
        int max = page * this.scrollSize + this.getPageSize();
        if (max > pageItemsSize) {
            max = pageItemsSize;
        }
        for (int i = page * this.scrollSize; i < max; ++i) {
            guiPage.add(this.getPageItems().get(i));
        }
        return guiPage;
    }
}

