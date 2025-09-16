/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package hu.geri.commands.subcommands;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;

public enum List {
    INSTANCE;


    public void execute(CommandSender sender) {
        CocoFFA plugin = CocoFFA.getInstance();
        ArrayList<String> arenaNames = new ArrayList<String>(plugin.getArenaManager().getArenaNames());
        if (arenaNames.isEmpty()) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.list.no-arenas"));
            return;
        }
        StringBuilder listMessage = new StringBuilder();
        listMessage.append(plugin.getLocaleManager().getMessage("commands.list.header"));
        for (String arenaName : arenaNames) {
            Arena arena = plugin.getArenaManager().getArena(arenaName);
            if (arena == null) continue;
            String arenaInfo = plugin.getLocaleManager().getMessage("commands.list.arena-info", "{arena_name}", arena.getDisplayName(), "{state}", arena.getState().name(), "{players}", String.valueOf(arena.getPlayers().size()), "{max_players}", String.valueOf(arena.getMaxPlayers()), "{enabled}", arena.isEnabled() ? "\u2713" : "\u2717");
            listMessage.append("\n").append(arenaInfo);
        }
        sender.sendMessage(listMessage.toString());
    }
}

