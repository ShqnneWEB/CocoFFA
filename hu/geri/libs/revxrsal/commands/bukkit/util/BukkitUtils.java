/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package hu.geri.libs.revxrsal.commands.bukkit.util;

import hu.geri.libs.revxrsal.commands.util.Preconditions;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public final class BukkitUtils {
    private static final boolean BRIGADIER = BukkitUtils.brigadierAvailable();

    private BukkitUtils() {
        Preconditions.cannotInstantiate(BukkitUtils.class);
    }

    private static boolean brigadierAvailable() {
        try {
            Class.forName("com.mojang.brigadier.arguments.StringArgumentType");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isBrigadierAvailable() {
        return BRIGADIER;
    }

    @NotNull
    public static String legacyColorize(@NotNull String text) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)text);
    }
}

