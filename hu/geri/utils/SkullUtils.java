/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.SkullMeta
 *  org.bukkit.profile.PlayerProfile
 *  org.bukkit.profile.PlayerTextures
 */
package hu.geri.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

public class SkullUtils {
    public static boolean isBase64Material(String material) {
        if (material == null || material.length() < 20) {
            return false;
        }
        try {
            Base64.getDecoder().decode(material);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static ItemStack createSkullFromBase64(String base64) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta)skull.getItemMeta();
        if (meta != null) {
            if (SkullUtils.setSkullWithModernAPI(meta, base64)) {
                skull.setItemMeta((ItemMeta)meta);
                return skull;
            }
            if (SkullUtils.setSkullWithReflection(meta, base64)) {
                skull.setItemMeta((ItemMeta)meta);
                return skull;
            }
        }
        return skull;
    }

    private static boolean setSkullWithModernAPI(SkullMeta meta, String base64) {
        try {
            Class.forName("org.bukkit.profile.PlayerProfile");
            String textureUrl = SkullUtils.extractTextureUrl(base64);
            if (textureUrl == null) {
                byte[] decodedBytes = Base64.getDecoder().decode(base64);
                String decodedString = new String(decodedBytes);
                textureUrl = SkullUtils.extractTextureUrl(decodedString);
            }
            if (textureUrl != null) {
                PlayerProfile profile = Bukkit.createPlayerProfile((UUID)UUID.randomUUID(), (String)"CustomHead");
                PlayerTextures textures = profile.getTextures();
                textures.setSkin(new URL(textureUrl));
                profile.setTextures(textures);
                meta.setOwnerProfile(profile);
                return true;
            }
        } catch (Exception exception) {
            // empty catch block
        }
        return false;
    }

    private static boolean setSkullWithReflection(SkullMeta meta, String base64) {
        try {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "CustomHead");
            String textureData = base64;
            if (!base64.contains("textures")) {
                String textureUrl = SkullUtils.extractTextureUrl(new String(Base64.getDecoder().decode(base64)));
                if (textureUrl == null) {
                    textureUrl = new String(Base64.getDecoder().decode(base64));
                }
                if (textureUrl != null && !textureUrl.startsWith("http")) {
                    return false;
                }
                String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + textureUrl + "\"}}}";
                textureData = Base64.getEncoder().encodeToString(json.getBytes());
            }
            profile.getProperties().put((Object)"textures", (Object)new Property("textures", textureData));
            try {
                Method setProfileMethod = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                setProfileMethod.setAccessible(true);
                setProfileMethod.invoke(meta, profile);
                return true;
            } catch (NoSuchMethodException e1) {
                Field profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, profile);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String extractTextureUrl(String input) {
        if (input == null) {
            return null;
        }
        try {
            int urlEnd;
            int urlStart = input.indexOf("\"url\":\"");
            if (urlStart != -1 && (urlEnd = input.indexOf("\"", urlStart += 7)) > urlStart) {
                return input.substring(urlStart, urlEnd);
            }
            if (input.startsWith("http://") || input.startsWith("https://")) {
                return input;
            }
        } catch (Exception exception) {
            // empty catch block
        }
        return null;
    }
}

