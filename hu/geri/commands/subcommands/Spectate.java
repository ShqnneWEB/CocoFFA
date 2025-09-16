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

public enum Spectate {
    INSTANCE;


    public void execute(Player player, String arenaName) {
        CocoFFA plugin = CocoFFA.getInstance();
        if (plugin.getArenaManager().getPlayerArena(player) != null) {
            player.sendMessage(plugin.getLocaleManager().getMessage("commands.spectate.already-in-arena"));
            return;
        }
        if (plugin.getSpectatorManager().isSpectating(player)) {
            player.sendMessage(plugin.getLocaleManager().getMessage("commands.spectate.already-spectating"));
            return;
        }
        Arena targetArena = plugin.getArenaManager().getArena(arenaName);
        if (targetArena == null) {
            player.sendMessage(plugin.getLocaleManager().getMessage("commands.spectate.arena-not-found", "{arena_name}", arenaName));
            return;
        }
        if (targetArena.getState() != ArenaState.STARTED) {
            player.sendMessage(plugin.getLocaleManager().getMessage("commands.spectate.arena-not-started", "{arena_name}", arenaName));
            return;
        }
        if (targetArena.getPlayers().isEmpty()) {
            player.sendMessage(plugin.getLocaleManager().getMessage("commands.spectate.arena-empty", "{arena_name}", arenaName));
            return;
        }
        plugin.getSpectatorManager().addSpectator(player, targetArena);
        player.sendMessage(plugin.getLocaleManager().getMessage("commands.spectate.success", "{arena_name}", targetArena.getDisplayName()));
    }
}

