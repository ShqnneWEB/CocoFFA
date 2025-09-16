/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 *  org.bukkit.Tag
 *  org.bukkit.block.banner.Pattern
 *  org.bukkit.block.banner.PatternType
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.BannerMeta
 *  org.bukkit.inventory.meta.ItemMeta
 */
package hu.geri.libs.gui.builder.item;

import hu.geri.libs.gui.builder.item.BaseItemBuilder;
import hu.geri.libs.gui.components.exception.GuiException;
import hu.geri.libs.gui.components.util.VersionHelper;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class BannerBuilder
extends BaseItemBuilder<BannerBuilder> {
    private static final Material DEFAULT_BANNER;
    private static final EnumSet<Material> BANNERS;

    BannerBuilder() {
        super(new ItemStack(DEFAULT_BANNER));
    }

    BannerBuilder(@NotNull ItemStack itemStack) {
        super(itemStack);
        if (!BANNERS.contains(itemStack.getType())) {
            throw new GuiException("BannerBuilder requires the material to be a banner!");
        }
    }

    @NotNull
    @Contract(value="_ -> this")
    public BannerBuilder baseColor(@NotNull DyeColor color) {
        BannerMeta bannerMeta = (BannerMeta)this.getMeta();
        bannerMeta.setBaseColor(color);
        this.setMeta((ItemMeta)bannerMeta);
        return this;
    }

    @NotNull
    @Contract(value="_, _ -> this")
    public BannerBuilder pattern(@NotNull DyeColor color, @NotNull PatternType pattern) {
        BannerMeta bannerMeta = (BannerMeta)this.getMeta();
        bannerMeta.addPattern(new Pattern(color, pattern));
        this.setMeta((ItemMeta)bannerMeta);
        return this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public BannerBuilder pattern(@NotNull Pattern ... pattern) {
        return this.pattern(Arrays.asList(pattern));
    }

    @NotNull
    @Contract(value="_ -> this")
    public BannerBuilder pattern(@NotNull List<Pattern> patterns) {
        BannerMeta bannerMeta = (BannerMeta)this.getMeta();
        for (Pattern it : patterns) {
            bannerMeta.addPattern(it);
        }
        this.setMeta((ItemMeta)bannerMeta);
        return this;
    }

    @NotNull
    @Contract(value="_, _, _ -> this")
    public BannerBuilder pattern(int index, @NotNull DyeColor color, @NotNull PatternType pattern) {
        return this.pattern(index, new Pattern(color, pattern));
    }

    @NotNull
    @Contract(value="_, _ -> this")
    public BannerBuilder pattern(int index, @NotNull Pattern pattern) {
        BannerMeta bannerMeta = (BannerMeta)this.getMeta();
        bannerMeta.setPattern(index, pattern);
        this.setMeta((ItemMeta)bannerMeta);
        return this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public BannerBuilder setPatterns(@NotNull @NotNull List<@NotNull Pattern> patterns) {
        BannerMeta bannerMeta = (BannerMeta)this.getMeta();
        bannerMeta.setPatterns(patterns);
        this.setMeta((ItemMeta)bannerMeta);
        return this;
    }

    static {
        if (VersionHelper.IS_ITEM_LEGACY) {
            DEFAULT_BANNER = Material.valueOf((String)"BANNER");
            BANNERS = EnumSet.of(Material.valueOf((String)"BANNER"));
        } else {
            DEFAULT_BANNER = Material.WHITE_BANNER;
            BANNERS = EnumSet.copyOf(Tag.BANNERS.getValues());
        }
    }
}

