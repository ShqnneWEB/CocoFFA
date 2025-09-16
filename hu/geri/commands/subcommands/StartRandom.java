/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package hu.geri.commands.subcommands;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.arena.ArenaState;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;

public enum StartRandom {
    INSTANCE;


    public void execute(CommandSender sender) {
        CocoFFA plugin = CocoFFA.getInstance();
        Set<String> arenaNameSet = plugin.getArenaManager().getArenaNames();
        List availableArenas = arenaNameSet.stream().map(name -> plugin.getArenaManager().getArena((String)name)).filter(arena -> arena != null && arena.isEnabled() && arena.getState() == ArenaState.STOPPED && arena.isConfigured()).collect(Collectors.toList());
        if (availableArenas.isEmpty()) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("startrandom.no-available"));
            return;
        }
        Random random = new Random();
        Arena randomArena = (Arena)availableArenas.get(random.nextInt(availableArenas.size()));
        if (!randomArena.isConfigured()) {
            List<String> missingSettings = randomArena.getMissingSettings();
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.start.missing-config", "{arena_name}", randomArena.getName()));
            plugin.getServer().getConsoleSender().sendMessage(plugin.getLocaleManager().getMessage("console.arena.missing-settings.header-with-arena", "{arena_name}", randomArena.getName()));
            for (String missing : missingSettings) {
                plugin.getServer().getConsoleSender().sendMessage(plugin.getLocaleManager().getMessage("console.arena.missing-settings.item", "{missing}", missing));
            }
            plugin.getServer().getConsoleSender().sendMessage(plugin.getLocaleManager().getMessage("console.arena.missing-settings.footer"));
            return;
        }
        plugin.getArenaManager().startArena(randomArena);
        sender.sendMessage(plugin.getLocaleManager().getMessage("startrandom.success", "{arena_name}", randomArena.getName()));
    }
}

