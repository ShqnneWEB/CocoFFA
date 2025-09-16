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

public enum Setup {
    INSTANCE;


    public void execute(Player player, String arenaName, String locationType) {
        CocoFFA plugin = CocoFFA.getInstance();
        Arena targetArena = plugin.getArenaManager().getArena(arenaName);
        if (targetArena == null) {
            player.sendMessage(plugin.getLocaleManager().getMessage("commands.join.not-found", "{arena_name}", arenaName));
            return;
        }
        switch (locationType.toLowerCase()) {
            case "start-location": {
                targetArena.setStartLocation(player.getLocation());
                player.sendMessage(plugin.getLocaleManager().getMessage("setup.start-location.success", "{arena_name}", arenaName));
                break;
            }
            case "exit-location": {
                targetArena.setExitLocation(player.getLocation());
                player.sendMessage(plugin.getLocaleManager().getMessage("setup.exit-location.success", "{arena_name}", arenaName));
                break;
            }
            case "border-center-location": {
                targetArena.setBorderCenterLocation(player.getLocation());
                player.sendMessage(plugin.getLocaleManager().getMessage("setup.border-center-location.success", "{arena_name}", arenaName));
                break;
            }
            default: {
                player.sendMessage(plugin.getLocaleManager().getMessage("setup.invalid-type"));
                return;
            }
        }
        plugin.getArenaManager().saveArena(targetArena);
    }
}

