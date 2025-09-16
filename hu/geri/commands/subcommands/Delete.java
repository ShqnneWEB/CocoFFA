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
import org.bukkit.command.CommandSender;

public enum Delete {
    INSTANCE;


    public void execute(CommandSender sender, String arena) {
        CocoFFA plugin = CocoFFA.getInstance();
        Arena targetArena = plugin.getArenaManager().getArena(arena);
        if (targetArena == null) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.join.not-found", "{arena_name}", arena));
            return;
        }
        if (targetArena.getState() == ArenaState.WAITING || targetArena.getState() == ArenaState.STARTED) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.delete.arena-running", "{arena_name}", arena));
            return;
        }
        if (plugin.getArenaManager().deleteArena(arena)) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.delete.success", "{arena_name}", arena));
        } else {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.delete.failed", "{arena_name}", arena));
        }
    }
}

