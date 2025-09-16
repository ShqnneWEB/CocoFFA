/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.gui.components.util;

import hu.geri.libs.gui.components.GuiType;
import hu.geri.libs.gui.components.exception.GuiException;
import hu.geri.libs.gui.guis.BaseGui;
import hu.geri.libs.gui.guis.GuiItem;
import hu.geri.libs.gui.guis.PaginatedGui;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class GuiFiller {
    private final BaseGui gui;

    public GuiFiller(BaseGui gui) {
        this.gui = gui;
    }

    public void fillTop(@NotNull GuiItem guiItem) {
        this.fillTop(Collections.singletonList(guiItem));
    }

    public void fillTop(@NotNull List<GuiItem> guiItems) {
        List<GuiItem> items = this.repeatList(guiItems);
        for (int i = 0; i < 9; ++i) {
            if (this.gui.getGuiItems().containsKey(i)) continue;
            this.gui.setItem(i, items.get(i));
        }
    }

    public void fillBottom(@NotNull GuiItem guiItem) {
        this.fillBottom(Collections.singletonList(guiItem));
    }

    public void fillBottom(@NotNull List<GuiItem> guiItems) {
        int rows = this.gui.getRows();
        List<GuiItem> items = this.repeatList(guiItems);
        for (int i = 9; i > 0; --i) {
            if (this.gui.getGuiItems().get(rows * 9 - i) != null) continue;
            this.gui.setItem(rows * 9 - i, items.get(i));
        }
    }

    public void fillBorder(@NotNull GuiItem guiItem) {
        this.fillBorder(Collections.singletonList(guiItem));
    }

    public void fillBorder(@NotNull List<GuiItem> guiItems) {
        int rows = this.gui.getRows();
        if (rows <= 2) {
            return;
        }
        List<GuiItem> items = this.repeatList(guiItems);
        for (int i = 0; i < rows * 9; ++i) {
            if (i > 8 && (i < rows * 9 - 8 || i > rows * 9 - 2) && i % 9 != 0 && i % 9 != 8) continue;
            this.gui.setItem(i, items.get(i));
        }
    }

    public void fillBetweenPoints(int rowFrom, int colFrom, int rowTo, int colTo, @NotNull GuiItem guiItem) {
        this.fillBetweenPoints(rowFrom, colFrom, rowTo, colTo, Collections.singletonList(guiItem));
    }

    public void fillBetweenPoints(int rowFrom, int colFrom, int rowTo, int colTo, @NotNull List<GuiItem> guiItems) {
        int minRow = Math.min(rowFrom, rowTo);
        int maxRow = Math.max(rowFrom, rowTo);
        int minCol = Math.min(colFrom, colTo);
        int maxCol = Math.max(colFrom, colTo);
        int rows = this.gui.getRows();
        List<GuiItem> items = this.repeatList(guiItems);
        for (int row = 1; row <= rows; ++row) {
            for (int col = 1; col <= 9; ++col) {
                int slot = this.getSlotFromRowCol(row, col);
                if (row < minRow || row > maxRow || col < minCol || col > maxCol) continue;
                this.gui.setItem(slot, items.get(slot));
            }
        }
    }

    public void fill(@NotNull GuiItem guiItem) {
        this.fill(Collections.singletonList(guiItem));
    }

    public void fill(@NotNull List<GuiItem> guiItems) {
        if (this.gui instanceof PaginatedGui) {
            throw new GuiException("Full filling a GUI is not supported in a Paginated GUI!");
        }
        GuiType type = this.gui.guiType();
        int fill = type == GuiType.CHEST ? this.gui.getRows() * type.getLimit() : type.getFillSize();
        List<GuiItem> items = this.repeatList(guiItems);
        for (int i = 0; i < fill; ++i) {
            if (this.gui.getGuiItems().get(i) != null) continue;
            this.gui.setItem(i, items.get(i));
        }
    }

    public void fillSide(@NotNull Side side, @NotNull List<GuiItem> guiItems) {
        switch (side.ordinal()) {
            case 0: {
                this.fillBetweenPoints(1, 1, this.gui.getRows(), 1, guiItems);
                break;
            }
            case 1: {
                this.fillBetweenPoints(1, 9, this.gui.getRows(), 9, guiItems);
                break;
            }
            case 2: {
                this.fillSide(Side.LEFT, guiItems);
                this.fillSide(Side.RIGHT, guiItems);
            }
        }
    }

    private List<GuiItem> repeatList(@NotNull List<GuiItem> guiItems) {
        ArrayList<GuiItem> repeated = new ArrayList<GuiItem>();
        Collections.nCopies(this.gui.getRows() * 9, guiItems).forEach(repeated::addAll);
        return repeated;
    }

    private int getSlotFromRowCol(int row, int col) {
        return col + (row - 1) * 9 - 1;
    }

    public static enum Side {
        LEFT,
        RIGHT,
        BOTH;

    }
}

