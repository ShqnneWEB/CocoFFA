/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Color
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.MapMeta
 *  org.bukkit.map.MapView
 */
package hu.geri.libs.gui.builder.item;

import hu.geri.libs.gui.builder.item.BaseItemBuilder;
import hu.geri.libs.gui.components.exception.GuiException;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MapBuilder
extends BaseItemBuilder<MapBuilder> {
    private static final Material MAP = Material.MAP;

    MapBuilder() {
        super(new ItemStack(MAP));
    }

    MapBuilder(@NotNull ItemStack itemStack) {
        super(itemStack);
        if (itemStack.getType() != MAP) {
            throw new GuiException("MapBuilder requires the material to be a MAP!");
        }
    }

    @Override
    @NotNull
    @Contract(value="_ -> this")
    public MapBuilder color(@Nullable Color color) {
        MapMeta mapMeta = (MapMeta)this.getMeta();
        mapMeta.setColor(color);
        this.setMeta((ItemMeta)mapMeta);
        return this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public MapBuilder locationName(@Nullable String name) {
        MapMeta mapMeta = (MapMeta)this.getMeta();
        mapMeta.setLocationName(name);
        this.setMeta((ItemMeta)mapMeta);
        return this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public MapBuilder scaling(boolean scaling) {
        MapMeta mapMeta = (MapMeta)this.getMeta();
        mapMeta.setScaling(scaling);
        this.setMeta((ItemMeta)mapMeta);
        return this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public MapBuilder view(@NotNull MapView view) {
        MapMeta mapMeta = (MapMeta)this.getMeta();
        mapMeta.setMapView(view);
        this.setMeta((ItemMeta)mapMeta);
        return this;
    }
}

