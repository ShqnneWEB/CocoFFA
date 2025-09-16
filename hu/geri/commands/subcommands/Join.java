/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package hu.geri.commands.subcommands;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.arena.ArenaState;
import org.bukkit.entity.Player;

public enum Join {
    INSTANCE;


    public void execute(Player player, String arenaName) {
        CocoFFA plugin = CocoFFA.getInstance();
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            player.sendMessage(plugin.getLocaleManager().getMessage("commands.join.not-found", "{arena_name}", arenaName));
            return;
        }
        if (!arena.isRunning()) {
            player.sendMessage(plugin.getLocaleManager().getMessage("commands.join.not-started", "{arena_name}", arena.getName()));
            return;
        }
        if (!arena.isEnabled()) {
            player.sendMessage(plugin.getLocaleManager().getMessage("commands.join.disabled", "{arena_name}", arena.getName()));
            return;
        }
        if (arena.getState() == ArenaState.STOPPED) {
            player.sendMessage(plugin.getLocaleManager().getMessage("commands.join.not-started", "{arena_name}", arena.getName()));
            return;
        }
        if (arena.getState() == ArenaState.STARTED) {
            player.sendMessage(plugin.getLocaleManager().getMessage("commands.join.already-started", "{arena_name}", arena.getName()));
            return;
        }
        if (plugin.getArenaManager().getPlayerArena(player) != null) {
            player.sendMessage(plugin.getLocaleManager().getMessage("commands.join.already-in-arena"));
            return;
        }
        if (!plugin.getMaxPlayersPerIPChecker().canPlayerJoinArena(player, arena)) {
            player.sendMessage(plugin.getMaxPlayersPerIPChecker().getIPLimitMessage());
            return;
        }
        plugin.getArenaManager().joinArena(player, arena);
        player.sendMessage(plugin.getLocaleManager().getMessage("commands.join.success", "{arena_name}", arena.getName()));
    }
}

