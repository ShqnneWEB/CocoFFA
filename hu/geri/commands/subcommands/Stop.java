/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package hu.geri.commands.subcommands;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import org.bukkit.command.CommandSender;

public enum Stop {
    INSTANCE;


    public void execute(CommandSender sender, String arenaName) {
        CocoFFA plugin = CocoFFA.getInstance();
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.join.not-found", "{arena_name}", arenaName));
            return;
        }
        if (!arena.isRunning()) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.stop.not-started", "{arena_name}", arena.getName()));
            return;
        }
        plugin.getArenaManager().stopArena(arena);
        sender.sendMessage(plugin.getLocaleManager().getMessage("commands.stop.success", "{arena_name}", arena.getName()));
    }
}

