/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.EnderPearl
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.ProjectileHitEvent
 */
package hu.geri.listeners;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class EnderpearlListener
implements Listener {
    private final CocoFFA plugin;

    public EnderpearlListener(CocoFFA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof EnderPearl)) {
            return;
        }
        EnderPearl enderPearl = (EnderPearl)event.getEntity();
        if (!(enderPearl.getShooter() instanceof Player)) {
            return;
        }
        Player player = (Player)enderPearl.getShooter();
        String arenaName = this.plugin.getArenaManager().getPlayerArena(player);
        if (arenaName == null) {
            return;
        }
        Arena arena = this.plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            return;
        }
        if (!this.plugin.getConfigManager().isEnderpearlRestrictionEnabled()) {
            return;
        }
        Location borderCenter = arena.getBorderCenterLocation();
        if (borderCenter == null) {
            return;
        }
        Location hitLocation = enderPearl.getLocation();
        double borderSize = arena.getCurrentBorderSize();
        double halfSize = borderSize / 2.0;
        double deltaX = Math.abs(hitLocation.getX() - borderCenter.getX());
        double deltaZ = Math.abs(hitLocation.getZ() - borderCenter.getZ());
        if (deltaX > halfSize || deltaZ > halfSize) {
            event.setCancelled(true);
            enderPearl.remove();
            player.sendMessage(this.plugin.getLocaleManager().getMessage("arena.enderpearl.restricted"));
        }
    }
}

