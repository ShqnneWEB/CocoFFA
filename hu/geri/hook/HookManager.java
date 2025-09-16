/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package hu.geri.hook;

import hu.geri.CocoFFA;
import hu.geri.hook.PlaceHolderAPIHook;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

public class HookManager {
    private static PlaceHolderAPIHook papi = null;

    public void updateHooks() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            papi = new PlaceHolderAPIHook();
            CocoFFA.getInstance().getLogger().info("\u001b[36m   [Hook] PlaceholderAPI successfully enabled.\u001b[0m");
        }
    }

    @Nullable
    public static PlaceHolderAPIHook getPapi() {
        return papi;
    }
}

