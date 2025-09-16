/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.managers;

import hu.geri.CocoFFA;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class VanishManager {
    private static final Set<UUID> vanishedPlayers = ConcurrentHashMap.newKeySet();

    public static void addVanished(Player player) {
        vanishedPlayers.add(player.getUniqueId());
        VanishManager.updatePlayerVisibility(player);
    }

    public static void removeVanished(Player player) {
        vanishedPlayers.remove(player.getUniqueId());
        VanishManager.updatePlayerVisibility(player);
    }

    public static boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    private static void updatePlayerVisibility(Player vanishedPlayer) {
        CocoFFA plugin = CocoFFA.getInstance();
        boolean staffCanSee = plugin.getConfigManager().canStaffSeeSpectators();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals((Object)vanishedPlayer)) continue;
            if (VanishManager.isVanished(vanishedPlayer)) {
                if (VanishManager.isVanished(online) || staffCanSee && online.hasPermission("cocoffa.admin")) {
                    online.showPlayer((Plugin)plugin, vanishedPlayer);
                    continue;
                }
                online.hidePlayer((Plugin)plugin, vanishedPlayer);
                continue;
            }
            online.showPlayer((Plugin)plugin, vanishedPlayer);
        }
    }

    public static void handlePlayerQuit(Player player) {
        VanishManager.removeVanished(player);
    }
}

