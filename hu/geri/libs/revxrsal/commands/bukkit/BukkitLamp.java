/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.java.JavaPlugin
 */
package hu.geri.libs.revxrsal.commands.bukkit;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.bukkit.BukkitLampConfig;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class BukkitLamp {
    public static Lamp.Builder<BukkitCommandActor> builder(@NotNull JavaPlugin plugin) {
        return BukkitLamp.builder(BukkitLampConfig.createDefault(plugin));
    }

    public static <A extends BukkitCommandActor> Lamp.Builder<A> builder(@NotNull BukkitLampConfig<A> config) {
        return Lamp.builder().accept(config);
    }
}

