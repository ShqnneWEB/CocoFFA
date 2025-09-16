/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 */
package hu.geri.commands.subcommands;

import hu.geri.CocoFFA;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public enum ResetWins {
    INSTANCE;


    public void execute(CommandSender sender, String playerName) {
        CocoFFA plugin = CocoFFA.getInstance();
        OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(playerName);
        if (targetPlayer == null || !targetPlayer.hasPlayedBefore()) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.wins.player-not-found", "{player}", playerName));
            return;
        }
        plugin.getDatabase().setWins(targetPlayer.getUniqueId(), 0);
        sender.sendMessage(plugin.getLocaleManager().getMessage("resetwins.success", "{player_name}", targetPlayer.getName()));
    }
}

