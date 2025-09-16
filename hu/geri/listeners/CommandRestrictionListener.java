/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 */
package hu.geri.listeners;

import hu.geri.CocoFFA;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandRestrictionListener
implements Listener {
    private final CocoFFA plugin;

    public CommandRestrictionListener(CocoFFA plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String arenaName;
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase();
        if (this.plugin.getSpectatorManager().isSpectating(player)) {
            String baseCommand = command.substring(1);
            if (baseCommand.contains(" ")) {
                baseCommand = baseCommand.split(" ")[0];
            }
            if (!baseCommand.equals("cocoffa") && !baseCommand.equals("ffa")) {
                event.setCancelled(true);
                player.sendMessage(this.plugin.getLocaleManager().getMessage("commands.spectate.command-blocked"));
                return;
            }
            if (!command.contains("leavespectate")) {
                event.setCancelled(true);
                player.sendMessage(this.plugin.getLocaleManager().getMessage("commands.spectate.command-blocked"));
                return;
            }
        }
        if ((arenaName = this.plugin.getArenaManager().getPlayerArena(player)) == null) {
            return;
        }
        if (!this.plugin.getConfigManager().isCommandRestrictionEnabled()) {
            return;
        }
        String bypassPermission = this.plugin.getConfigManager().getCommandBypassPermission();
        String bypassDefault = this.plugin.getConfigManager().getPermissionDefault("command-bypass");
        boolean hasBypass = false;
        if (player.hasPermission(bypassPermission)) {
            hasBypass = true;
        } else if ("player".equals(bypassDefault)) {
            hasBypass = true;
        } else if ("op".equals(bypassDefault) && player.isOp()) {
            hasBypass = true;
        }
        if (hasBypass) {
            return;
        }
        String baseCommand = command.substring(1);
        if (baseCommand.contains(" ")) {
            baseCommand = baseCommand.split(" ")[0];
        }
        List<String> allowedCommands = this.plugin.getConfigManager().getAllowedCommands();
        boolean isAllowed = false;
        for (String allowedCommand : allowedCommands) {
            if (!baseCommand.equals(allowedCommand.toLowerCase())) continue;
            isAllowed = true;
            break;
        }
        if (!isAllowed) {
            event.setCancelled(true);
            player.sendMessage(this.plugin.getLocaleManager().getMessage("commands.restricted-in-arena", "{command}", baseCommand, "{arena_name}", arenaName));
        }
    }
}

