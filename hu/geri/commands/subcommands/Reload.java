/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package hu.geri.commands.subcommands;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import java.util.Map;
import org.bukkit.command.CommandSender;

public enum Reload {
    INSTANCE;


    public void execute(CommandSender sender) {
        CocoFFA plugin = CocoFFA.getInstance();
        plugin.getServer().getConsoleSender().sendMessage(plugin.getLocaleManager().colorize("&#FFEE00[CocoFFA] &#FFEE00Reloading configuration..."));
        if (!plugin.getConfigManager().reloadConfig()) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.reload.failed", "{file}", "config.yml"));
            return;
        }
        plugin.getServer().getConsoleSender().sendMessage(plugin.getLocaleManager().getMessage("commands.reload.config"));
        plugin.getLocaleManager().loadLocale();
        plugin.getServer().getConsoleSender().sendMessage(plugin.getLocaleManager().getMessage("commands.reload.locale"));
        plugin.getHookManager().updateHooks();
        for (Map.Entry<String, Arena> entry : plugin.getArenaManager().getArenas().entrySet()) {
            Arena arena = entry.getValue();
            if (arena.reload()) continue;
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.reload.failed", "{file}", "arenas/" + arena.getName() + ".yml"));
            return;
        }
        plugin.getArenaManager().loadArenas(true);
        int arenaCount = plugin.getArenaManager().getArenaNames().size();
        plugin.getServer().getConsoleSender().sendMessage(plugin.getLocaleManager().getMessage("commands.reload.arenas", "{count}", String.valueOf(arenaCount)));
        plugin.getArenaTickManager().restart();
        if (plugin.getMainEditorGUI() != null) {
            plugin.getMainEditorGUI().reloadGuiConfig();
        }
        if (plugin.getRewardEditorGUI() != null) {
            plugin.getRewardEditorGUI().reloadGuiConfig();
        }
        if (plugin.getScoreboardEditorGUI() != null) {
            plugin.getScoreboardEditorGUI().reloadGuiConfig();
        }
        plugin.getServer().getConsoleSender().sendMessage(plugin.getLocaleManager().getMessage("commands.reload.gui"));
        if (plugin.getWebhookManager() != null) {
            plugin.getWebhookManager().reloadWebhookConfig();
        }
        plugin.getServer().getConsoleSender().sendMessage(plugin.getLocaleManager().getMessage("commands.reload.webhook"));
        sender.sendMessage(plugin.getLocaleManager().getMessage("commands.reload.success"));
    }
}

