/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.entity.Player
 */
package hu.geri.libs.revxrsal.commands.bukkit.sender;

import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandParameter;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.process.SenderResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class BukkitSenderResolver
implements SenderResolver<BukkitCommandActor> {
    @Override
    public boolean isSenderType(@NotNull CommandParameter parameter) {
        return CommandSender.class.isAssignableFrom(parameter.type());
    }

    @Override
    @NotNull
    public Object getSender(@NotNull Class<?> customSenderType, @NotNull BukkitCommandActor actor, @NotNull ExecutableCommand<BukkitCommandActor> command) {
        if (Player.class.isAssignableFrom(customSenderType)) {
            return actor.requirePlayer();
        }
        if (ConsoleCommandSender.class.isAssignableFrom(customSenderType)) {
            return actor.requireConsole();
        }
        return actor.sender();
    }
}

