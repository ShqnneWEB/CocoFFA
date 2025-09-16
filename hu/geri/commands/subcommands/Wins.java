/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package hu.geri.commands.subcommands;

import hu.geri.CocoFFA;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Wins {
    INSTANCE;


    public void execute(CommandSender sender, String playerName) {
        OfflinePlayer targetPlayer;
        CocoFFA plugin = CocoFFA.getInstance();
        if (playerName == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getLocaleManager().getMessage("player-only"));
                return;
            }
            playerName = ((Player)sender).getName();
        }
        if ((targetPlayer = plugin.getServer().getOfflinePlayer(playerName)) == null || !targetPlayer.hasPlayedBefore()) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.wins.player-not-found", "{player}", playerName));
            return;
        }
        plugin.getDatabase().getWins(targetPlayer.getUniqueId()).thenAccept(wins -> {
            String message = plugin.getLocaleManager().getMessage("commands.wins.result", "{player}", targetPlayer.getName(), "{wins}", String.valueOf(wins));
            sender.sendMessage(message);
        });
    }
}

