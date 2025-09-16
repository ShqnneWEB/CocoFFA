/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package hu.geri.commands.subcommands;

import hu.geri.CocoFFA;
import org.bukkit.entity.Player;

public enum LeaveSpectate {
    INSTANCE;


    public void execute(Player player) {
        CocoFFA plugin = CocoFFA.getInstance();
        if (!plugin.getSpectatorManager().isSpectating(player)) {
            player.sendMessage(plugin.getLocaleManager().getMessage("commands.leavespectate.not-spectating"));
            return;
        }
        String arenaName = plugin.getSpectatorManager().getSpectatingArena(player);
        plugin.getSpectatorManager().removeSpectator(player);
        player.sendMessage(plugin.getLocaleManager().getMessage("commands.leavespectate.success", "{arena_name}", arenaName != null ? arenaName : "Unknown"));
    }
}

