/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  me.clip.placeholderapi.PlaceholderAPI
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PlaceHolderAPIHook {
    public static String parsePlaceholders(Player player, @NotNull String string) {
        if (PlaceHolderAPIHook.isPlaceholderAPIEnabled()) {
            return PlaceholderAPI.setPlaceholders((Player)player, (String)string);
        }
        return string;
    }

    public static boolean isPlaceholderAPIEnabled() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        return plugin != null && plugin.isEnabled();
    }
}

