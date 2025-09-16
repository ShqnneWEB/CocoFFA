/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.commands.handler;

import hu.geri.CocoFFA;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import hu.geri.libs.revxrsal.commands.bukkit.exception.InvalidPlayerException;
import hu.geri.libs.revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import hu.geri.libs.revxrsal.commands.exception.MissingArgumentException;
import hu.geri.libs.revxrsal.commands.exception.NoPermissionException;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import org.jetbrains.annotations.NotNull;

public class CommandExceptionHandler
extends BukkitExceptionHandler {
    private final CocoFFA plugin;

    public CommandExceptionHandler(CocoFFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onInvalidPlayer(InvalidPlayerException exception, @NotNull BukkitCommandActor actor) {
        String message = this.plugin.getLocaleManager().getMessage("commands.invalid-player");
        if (message == null || message.isEmpty()) {
            message = "&cInvalid player specified.";
        }
        actor.error(message);
    }

    @Override
    public void onSenderNotPlayer(SenderNotPlayerException exception, @NotNull BukkitCommandActor actor) {
        String message = this.plugin.getLocaleManager().getMessage("commands.player-required");
        if (message == null || message.isEmpty()) {
            message = "&cThis command can only be executed by players.";
        }
        actor.error(message);
    }

    @Override
    public void onMissingArgument(@NotNull MissingArgumentException exception, @NotNull BukkitCommandActor actor, @NotNull ParameterNode<BukkitCommandActor, ?> parameter) {
        String usage = parameter.command().usage();
        Object message = this.plugin.getLocaleManager().getMessage("commands.missing-argument", "{usage}", usage);
        if (message == null || ((String)message).isEmpty()) {
            message = "&cMissing argument. Usage: " + usage;
        }
        actor.error((String)message);
    }

    @Override
    public void onNoPermission(@NotNull NoPermissionException exception, @NotNull BukkitCommandActor actor) {
        String message = this.plugin.getLocaleManager().getMessage("commands.no-permission");
        if (message == null || message.isEmpty()) {
            message = "&cYou don't have permission to execute this command.";
        }
        actor.error(message);
    }
}

