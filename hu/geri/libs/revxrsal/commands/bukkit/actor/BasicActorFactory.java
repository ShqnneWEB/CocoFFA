/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.libs.revxrsal.commands.bukkit.actor;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.bukkit.actor.ActorFactory;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BasicBukkitActor;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.process.MessageSender;
import java.util.Optional;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

final class BasicActorFactory
implements ActorFactory<BukkitCommandActor> {
    private final Plugin plugin;
    private final Optional<BukkitAudiences> bukkitAudiences;
    private final MessageSender<BukkitCommandActor, ComponentLike> messageSender;

    public BasicActorFactory(Plugin plugin, Optional<BukkitAudiences> bukkitAudiences) {
        this(plugin, bukkitAudiences, null);
    }

    public BasicActorFactory(Plugin plugin, Optional<BukkitAudiences> bukkitAudiences, MessageSender<BukkitCommandActor, ComponentLike> messageSender) {
        this.plugin = plugin;
        this.bukkitAudiences = bukkitAudiences;
        this.messageSender = messageSender;
    }

    @Override
    @NotNull
    public BukkitCommandActor create(@NotNull CommandSender sender, @NotNull Lamp<BukkitCommandActor> lamp) {
        return new BasicBukkitActor(sender, this.plugin, this.bukkitAudiences, this.messageSender, lamp);
    }
}

