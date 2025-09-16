/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package hu.geri.commands.subcommands;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import org.bukkit.entity.Player;

public enum Leave {
    INSTANCE;


    public void execute(Player player) {
        CocoFFA plugin = CocoFFA.getInstance();
        String arenaName = plugin.getArenaManager().getPlayerArena(player);
        if (arenaName == null) {
            player.sendMessage(plugin.getLocaleManager().getMessage("commands.leave.not-in-arena"));
            return;
        }
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        plugin.getArenaManager().leaveArena(player);
        player.sendMessage(plugin.getLocaleManager().getMessage("commands.leave.success", "{arena_name}", arena != null ? arena.getName() : arenaName));
    }
}

