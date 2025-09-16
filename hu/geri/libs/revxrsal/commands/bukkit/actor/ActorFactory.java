/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package hu.geri.libs.revxrsal.commands.bukkit.actor;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BasicActorFactory;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.process.MessageSender;
import java.util.Optional;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ActorFactory<A extends BukkitCommandActor> {
    @NotNull
    public static ActorFactory<BukkitCommandActor> defaultFactory(@NotNull Plugin plugin, @NotNull Optional<BukkitAudiences> audiences) {
        return new BasicActorFactory(plugin, audiences);
    }

    public static <A extends BukkitCommandActor> Object defaultFactory(@NotNull JavaPlugin plugin, @NotNull Optional<BukkitAudiences> audiences, @Nullable MessageSender<? super A, ComponentLike> messageSender) {
        return new BasicActorFactory((Plugin)plugin, audiences, messageSender);
    }

    @NotNull
    public A create(@NotNull CommandSender var1, @NotNull Lamp<A> var2);
}

