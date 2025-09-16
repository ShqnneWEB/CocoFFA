/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package hu.geri.listeners;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MaxPlayersPerIPListener {
    private final CocoFFA plugin;

    public MaxPlayersPerIPListener(CocoFFA plugin) {
        this.plugin = plugin;
    }

    public boolean canPlayerJoinArena(Player joiningPlayer, Arena targetArena) {
        if (!this.plugin.getConfig().getBoolean("max-players-per-ip.enabled", false)) {
            return true;
        }
        String bypassPermission = this.plugin.getConfig().getString("permissions.ip-limit-bypass.permission", "cocoffa.iplimit.bypass");
        if (joiningPlayer.hasPermission(bypassPermission)) {
            return true;
        }
        int maxPlayersPerIP = this.plugin.getConfig().getInt("max-players-per-ip.max", 1);
        String playerIP = joiningPlayer.getAddress().getAddress().getHostAddress();
        int count = 0;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String arenaName;
            if (onlinePlayer.getAddress() == null || !onlinePlayer.getAddress().getAddress().getHostAddress().equals(playerIP) || (arenaName = this.plugin.getArenaManager().getPlayerArena(onlinePlayer)) == null || !arenaName.equals(targetArena.getName())) continue;
            ++count;
        }
        return count < maxPlayersPerIP;
    }

    public String getIPLimitMessage() {
        int maxPlayersPerIP = this.plugin.getConfig().getInt("max-players-per-ip.max", 1);
        return this.plugin.getLocaleManager().getMessage("commands.join.ip-limit-reached", "{max}", String.valueOf(maxPlayersPerIP));
    }
}

