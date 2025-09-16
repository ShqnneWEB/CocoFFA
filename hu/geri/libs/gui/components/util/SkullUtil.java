/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package hu.geri.libs.gui.components.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hu.geri.libs.gui.components.util.VersionHelper;
import java.util.Base64;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class SkullUtil {
    private static final Material SKULL = SkullUtil.getSkullMaterial();
    private static final Gson GSON = new Gson();

    private static Material getSkullMaterial() {
        if (VersionHelper.IS_ITEM_LEGACY) {
            return Material.valueOf((String)"SKULL_ITEM");
        }
        return Material.PLAYER_HEAD;
    }

    public static ItemStack skull() {
        return VersionHelper.IS_ITEM_LEGACY ? new ItemStack(SKULL, 1, 3) : new ItemStack(SKULL);
    }

    public static boolean isPlayerSkull(@NotNull ItemStack item) {
        if (VersionHelper.IS_ITEM_LEGACY) {
            return item.getType() == SKULL && item.getDurability() == 3;
        }
        return item.getType() == SKULL;
    }

    public static String getSkinUrl(String base64Texture) {
        String decoded = new String(Base64.getDecoder().decode(base64Texture));
        JsonObject object = GSON.fromJson(decoded, JsonObject.class);
        JsonElement textures = object.get("textures");
        if (textures == null) {
            return null;
        }
        JsonElement skin = textures.getAsJsonObject().get("SKIN");
        if (skin == null) {
            return null;
        }
        JsonElement url = skin.getAsJsonObject().get("url");
        return url == null ? null : url.getAsString();
    }
}

