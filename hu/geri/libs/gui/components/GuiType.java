/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.event.inventory.InventoryType
 */
package hu.geri.libs.gui.components;

import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public enum GuiType {
    CHEST(InventoryType.CHEST, 9, 9),
    WORKBENCH(InventoryType.WORKBENCH, 9, 10),
    HOPPER(InventoryType.HOPPER, 5, 5),
    DISPENSER(InventoryType.DISPENSER, 8, 9),
    BREWING(InventoryType.BREWING, 4, 5);

    @NotNull
    private final InventoryType inventoryType;
    private final int limit;
    private final int fillSize;

    private GuiType(InventoryType inventoryType, int limit, int fillSize) {
        this.inventoryType = inventoryType;
        this.limit = limit;
        this.fillSize = fillSize;
    }

    @NotNull
    public InventoryType getInventoryType() {
        return this.inventoryType;
    }

    public int getLimit() {
        return this.limit;
    }

    public int getFillSize() {
        return this.fillSize;
    }
}

