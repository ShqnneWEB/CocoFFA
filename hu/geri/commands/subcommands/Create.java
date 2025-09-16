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

public enum Create {
    INSTANCE;


    public void execute(CommandSender sender, String arena) {
        CocoFFA plugin = CocoFFA.getInstance();
        if (plugin.getArenaManager().getArena(arena) != null) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.create.already-exists", "{arena_name}", arena));
            return;
        }
        Arena createdArena = plugin.getArenaManager().createArena(arena);
        if (createdArena != null) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.create.success", "{arena_name}", arena));
        } else {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.create.failed", "{arena_name}", arena));
        }
    }
}

