/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package hu.geri.commands.subcommands;

import hu.geri.CocoFFA;
import org.bukkit.command.CommandSender;

public enum StopAll {
    INSTANCE;


    public void execute(CommandSender sender) {
        CocoFFA plugin = CocoFFA.getInstance();
        int stoppedCount = plugin.getArenaManager().stopAllArenas();
        sender.sendMessage(plugin.getLocaleManager().getMessage("commands.stopall.success", "{count}", String.valueOf(stoppedCount)));
    }
}

