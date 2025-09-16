/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package hu.geri.commands.subcommands;

import hu.geri.CocoFFA;
import org.bukkit.command.CommandSender;

public enum Help {
    INSTANCE;


    public void execute(CommandSender sender) {
        CocoFFA plugin = CocoFFA.getInstance();
        String helpMessage = plugin.getLocaleManager().getMessage("commands.help");
        String mainCommand = plugin.getConfig().getString("command.main", "ffa");
        helpMessage = helpMessage.replace("{command}", mainCommand);
        sender.sendMessage(helpMessage);
    }
}

