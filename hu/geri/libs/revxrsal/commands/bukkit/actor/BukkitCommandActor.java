/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.entity.Player
 */
package hu.geri.libs.revxrsal.commands.bukkit.actor;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.bukkit.exception.SenderNotConsoleException;
import hu.geri.libs.revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import java.util.Optional;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BukkitCommandActor
extends CommandActor {
    @NotNull
    public CommandSender sender();

    default public boolean isPlayer() {
        return this.sender() instanceof Player;
    }

    default public boolean isConsole() {
        return this.sender() instanceof ConsoleCommandSender;
    }

    @Nullable
    default public Player asPlayer() {
        return this.isPlayer() ? (Player)this.sender() : null;
    }

    @NotNull
    default public Player requirePlayer() throws SenderNotPlayerException {
        if (!this.isPlayer()) {
            throw new SenderNotPlayerException();
        }
        return (Player)this.sender();
    }

    @NotNull
    default public ConsoleCommandSender requireConsole() throws SenderNotConsoleException {
        if (!this.isConsole()) {
            throw new SenderNotConsoleException();
        }
        return (ConsoleCommandSender)this.sender();
    }

    @Override
    default public void sendRawMessage(@NotNull String message) {
        this.sender().sendMessage(message);
    }

    @Override
    default public void sendRawError(@NotNull String message) {
        this.sender().sendMessage(ChatColor.RED + message);
    }

    public void reply(@NotNull ComponentLike var1);

    @NotNull
    public Optional<Audience> audience();

    public Lamp<BukkitCommandActor> lamp();

    @Override
    @NotNull
    default public String name() {
        return this.sender().getName();
    }
}

