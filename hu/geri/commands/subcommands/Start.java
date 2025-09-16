/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package hu.geri.commands.subcommands;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.arena.ArenaState;
import java.util.List;
import org.bukkit.command.CommandSender;

public enum Start {
    INSTANCE;


    public void execute(CommandSender sender, String arena) {
        CocoFFA plugin = CocoFFA.getInstance();
        Arena targetArena = plugin.getArenaManager().getArena(arena);
        if (targetArena == null) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.join.not-found", "{arena_name}", arena));
            return;
        }
        if (!targetArena.isConfigured()) {
            List<String> missingSettings = targetArena.getMissingSettings();
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.start.missing-config", "{arena_name}", arena));
            plugin.getServer().getConsoleSender().sendMessage(plugin.getLocaleManager().getMessage("console.arena.missing-settings.header"));
            for (String missing : missingSettings) {
                plugin.getServer().getConsoleSender().sendMessage(plugin.getLocaleManager().getMessage("console.arena.missing-settings.item", "{missing}", missing));
            }
            plugin.getServer().getConsoleSender().sendMessage(plugin.getLocaleManager().getMessage("console.arena.missing-settings.footer"));
            return;
        }
        if (targetArena.getState() != ArenaState.STOPPED) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.start.already-started", "{arena_name}", arena));
            return;
        }
        plugin.getArenaManager().startArena(targetArena);
        sender.sendMessage(plugin.getLocaleManager().getMessage("commands.start.success", "{arena_name}", arena));
    }
}

