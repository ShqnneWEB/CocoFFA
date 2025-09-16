/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package hu.geri.libs.gui;

import hu.geri.libs.gui.guis.BaseGui;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class TriumphGui {
    private static Plugin PLUGIN = null;

    private TriumphGui() {
    }

    public static void init(@NotNull Plugin plugin) {
        PLUGIN = plugin;
    }

    @NotNull
    public static Plugin getPlugin() {
        if (PLUGIN == null) {
            TriumphGui.init((Plugin)JavaPlugin.getProvidingPlugin(BaseGui.class));
        }
        return PLUGIN;
    }
}

