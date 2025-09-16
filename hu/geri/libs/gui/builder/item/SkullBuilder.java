/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.SkullMeta
 *  org.bukkit.profile.PlayerProfile
 *  org.bukkit.profile.PlayerTextures
 */
package hu.geri.libs.gui.builder.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import hu.geri.libs.gui.builder.item.BaseItemBuilder;
import hu.geri.libs.gui.components.exception.GuiException;
import hu.geri.libs.gui.components.util.SkullUtil;
import hu.geri.libs.gui.components.util.VersionHelper;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class SkullBuilder
extends BaseItemBuilder<SkullBuilder> {
    private static final Field PROFILE_FIELD;

    SkullBuilder() {
        super(SkullUtil.skull());
    }

    SkullBuilder(@NotNull ItemStack itemStack) {
        super(itemStack);
        if (!SkullUtil.isPlayerSkull(itemStack)) {
            throw new GuiException("SkullBuilder requires the material to be a PLAYER_HEAD/SKULL_ITEM!");
        }
    }

    @NotNull
    @Contract(value="_, _ -> this")
    public SkullBuilder texture(@NotNull String texture, @NotNull UUID profileId) {
        if (!SkullUtil.isPlayerSkull(this.getItemStack())) {
            return this;
        }
        if (VersionHelper.IS_PLAYER_PROFILE_API) {
            String textureUrl = SkullUtil.getSkinUrl(texture);
            if (textureUrl == null) {
                return this;
            }
            SkullMeta skullMeta = (SkullMeta)this.getMeta();
            PlayerProfile profile = Bukkit.createPlayerProfile((UUID)profileId, (String)"");
            PlayerTextures textures = profile.getTextures();
            try {
                textures.setSkin(new URL(textureUrl));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return this;
            }
            profile.setTextures(textures);
            skullMeta.setOwnerProfile(profile);
            this.setMeta((ItemMeta)skullMeta);
            return this;
        }
        if (PROFILE_FIELD == null) {
            return this;
        }
        SkullMeta skullMeta = (SkullMeta)this.getMeta();
        GameProfile profile = new GameProfile(profileId, "");
        profile.getProperties().put((Object)"textures", (Object)new Property("textures", texture));
        try {
            PROFILE_FIELD.set(skullMeta, profile);
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        this.setMeta((ItemMeta)skullMeta);
        return this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public SkullBuilder texture(@NotNull String texture) {
        return this.texture(texture, UUID.randomUUID());
    }

    @NotNull
    @Contract(value="_ -> this")
    public SkullBuilder owner(@NotNull OfflinePlayer player) {
        if (!SkullUtil.isPlayerSkull(this.getItemStack())) {
            return this;
        }
        SkullMeta skullMeta = (SkullMeta)this.getMeta();
        if (VersionHelper.IS_SKULL_OWNER_LEGACY) {
            skullMeta.setOwner(player.getName());
        } else {
            skullMeta.setOwningPlayer(player);
        }
        this.setMeta((ItemMeta)skullMeta);
        return this;
    }

    static {
        Field field;
        try {
            SkullMeta skullMeta = (SkullMeta)SkullUtil.skull().getItemMeta();
            field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            field = null;
        }
        PROFILE_FIELD = field;
    }
}

