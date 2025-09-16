/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.persistence.PersistentDataType
 */
package hu.geri.commands.subcommands;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public enum GiveStarter {
    INSTANCE;


    public void execute(CommandSender sender, String arenaName, String playerName, Integer amount) {
        CocoFFA plugin = CocoFFA.getInstance();
        if (amount == null) {
            amount = 1;
        }
        if (amount < 1) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.givestarter.invalid-amount"));
            return;
        }
        Arena targetArena = plugin.getArenaManager().getArena(arenaName);
        if (targetArena == null) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.join.not-found", "{arena_name}", arenaName));
            return;
        }
        Player targetPlayer = plugin.getServer().getPlayer(playerName);
        if (targetPlayer == null) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.wins.player-not-found", "{player}", playerName));
            return;
        }
        if (!targetArena.isStarterEnabled()) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("givestarter.disabled", "{arena_name}", arenaName));
            return;
        }
        ItemStack starterItem = this.createStarterItem(targetArena, targetPlayer.getName());
        starterItem.setAmount(amount.intValue());
        targetPlayer.getInventory().addItem(new ItemStack[]{starterItem});
        String gaveMessage = plugin.getLocaleManager().getMessage("givestarter.gave").replace("{player}", targetPlayer.getName()).replace("{arena_name}", arenaName).replace("{amount}", String.valueOf(amount));
        sender.sendMessage(gaveMessage);
        String receivedMessage = plugin.getLocaleManager().getMessage("givestarter.received").replace("{player}", targetPlayer.getName()).replace("{arena_name}", arenaName).replace("{amount}", String.valueOf(amount));
        targetPlayer.sendMessage(receivedMessage);
    }

    private ItemStack createStarterItem(Arena arena, String playerName) {
        ItemMeta meta;
        CocoFFA plugin = CocoFFA.getInstance();
        ItemStack starterItem = new ItemStack(Material.valueOf((String)arena.getStarterMaterial()));
        if (starterItem.getType() != Material.AIR && (meta = starterItem.getItemMeta()) != null) {
            String displayName = arena.getStarterName().replace("{arena_name}", arena.getDisplayName()).replace("{player}", playerName);
            meta.setDisplayName(plugin.getLocaleManager().colorize(displayName));
            ArrayList<String> processedLore = new ArrayList<String>();
            for (String line : arena.getStarterLore()) {
                String processedLine = line.replace("{arena_name}", arena.getDisplayName()).replace("{player}", playerName);
                processedLore.add(plugin.getLocaleManager().colorize(processedLine));
            }
            meta.setLore(processedLore);
            if (arena.isStarterGlow()) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            }
            meta.getPersistentDataContainer().set(plugin.getStarterItemListener().getStarterKey(), PersistentDataType.STRING, (Object)arena.getName());
            starterItem.setItemMeta(meta);
        }
        return starterItem;
    }
}

