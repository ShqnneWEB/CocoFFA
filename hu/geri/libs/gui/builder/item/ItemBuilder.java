/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  org.bukkit.Material
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.SkullMeta
 */
package hu.geri.libs.gui.builder.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import hu.geri.libs.gui.builder.item.BannerBuilder;
import hu.geri.libs.gui.builder.item.BaseItemBuilder;
import hu.geri.libs.gui.builder.item.BookBuilder;
import hu.geri.libs.gui.builder.item.FireworkBuilder;
import hu.geri.libs.gui.builder.item.MapBuilder;
import hu.geri.libs.gui.builder.item.SkullBuilder;
import hu.geri.libs.gui.components.util.SkullUtil;
import java.lang.reflect.Field;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ItemBuilder
extends BaseItemBuilder<ItemBuilder> {
    ItemBuilder(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @NotNull
    @Contract(value="_ -> new")
    public static ItemBuilder from(@NotNull ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    @NotNull
    @Contract(value="_ -> new")
    public static ItemBuilder from(@NotNull Material material) {
        return new ItemBuilder(new ItemStack(material));
    }

    @NotNull
    @Contract(value=" -> new")
    public static BannerBuilder banner() {
        return new BannerBuilder();
    }

    @NotNull
    @Contract(value="_ -> new")
    public static BannerBuilder banner(@NotNull ItemStack itemStack) {
        return new BannerBuilder(itemStack);
    }

    @NotNull
    @Contract(value="_ -> new")
    public static BookBuilder book(@NotNull ItemStack itemStack) {
        return new BookBuilder(itemStack);
    }

    @NotNull
    @Contract(value=" -> new")
    public static FireworkBuilder firework() {
        return new FireworkBuilder(new ItemStack(Material.FIREWORK_ROCKET));
    }

    @NotNull
    @Contract(value="_ -> new")
    public static FireworkBuilder firework(@NotNull ItemStack itemStack) {
        return new FireworkBuilder(itemStack);
    }

    @NotNull
    @Contract(value=" -> new")
    public static MapBuilder map() {
        return new MapBuilder();
    }

    @NotNull
    @Contract(value="_ -> new")
    public static MapBuilder map(@NotNull ItemStack itemStack) {
        return new MapBuilder(itemStack);
    }

    @NotNull
    @Contract(value=" -> new")
    public static SkullBuilder skull() {
        return new SkullBuilder();
    }

    @NotNull
    @Contract(value="_ -> new")
    public static SkullBuilder skull(@NotNull ItemStack itemStack) {
        return new SkullBuilder(itemStack);
    }

    @NotNull
    @Contract(value=" -> new")
    public static FireworkBuilder star() {
        return new FireworkBuilder(new ItemStack(Material.FIREWORK_STAR));
    }

    @NotNull
    @Contract(value="_ -> new")
    public static FireworkBuilder star(@NotNull ItemStack itemStack) {
        return new FireworkBuilder(itemStack);
    }

    @Deprecated
    public ItemBuilder setSkullTexture(@NotNull String texture) {
        if (!SkullUtil.isPlayerSkull(this.getItemStack())) {
            return this;
        }
        SkullMeta skullMeta = (SkullMeta)this.getMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put((Object)"textures", (Object)new Property("textures", texture));
        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
        this.setMeta((ItemMeta)skullMeta);
        return this;
    }

    @Deprecated
    public ItemBuilder setSkullOwner(@NotNull OfflinePlayer player) {
        if (!SkullUtil.isPlayerSkull(this.getItemStack())) {
            return this;
        }
        SkullMeta skullMeta = (SkullMeta)this.getMeta();
        skullMeta.setOwningPlayer(player);
        this.setMeta((ItemMeta)skullMeta);
        return this;
    }
}

