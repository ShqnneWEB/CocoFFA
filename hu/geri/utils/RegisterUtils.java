/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 */
package hu.geri.utils;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.commands.FFACommands;
import hu.geri.commands.annotations.ArenaNames;
import hu.geri.commands.annotations.EnabledArenas;
import hu.geri.commands.annotations.LocationTypes;
import hu.geri.commands.annotations.OnlinePlayers;
import hu.geri.commands.annotations.RunningArenas;
import hu.geri.commands.handler.CommandExceptionHandler;
import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.bukkit.BukkitLamp;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.orphan.Orphans;
import hu.geri.listeners.ArenaListener;
import hu.geri.listeners.CommandRestrictionListener;
import hu.geri.listeners.EnderpearlListener;
import hu.geri.listeners.ItemDropListener;
import hu.geri.listeners.SpectatorItemListener;
import hu.geri.listeners.VanishListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public final class RegisterUtils {
    private static final CocoFFA plugin = CocoFFA.getInstance();

    private RegisterUtils() {
    }

    public static void registerCommands() {
        try {
            Lamp<BukkitCommandActor> lamp = BukkitLamp.builder(plugin).suggestionProviders(providers -> {
                providers.addProviderForAnnotation(OnlinePlayers.class, p -> ctx -> RegisterUtils.getOnlinePlayerNames());
                providers.addProviderForAnnotation(ArenaNames.class, a -> ctx -> RegisterUtils.getArenaNames());
                providers.addProviderForAnnotation(EnabledArenas.class, e -> ctx -> RegisterUtils.getEnabledArenaNames());
                providers.addProviderForAnnotation(RunningArenas.class, r -> ctx -> RegisterUtils.getRunningArenaNames());
                providers.addProviderForAnnotation(LocationTypes.class, l -> ctx -> RegisterUtils.getLocationTypes());
            }).exceptionHandler(new CommandExceptionHandler(plugin)).build();
            ArrayList commandAliases = new ArrayList();
            commandAliases.add(plugin.getConfigManager().getMainCommand());
            commandAliases.addAll(plugin.getConfigManager().getCommandAliases());
            lamp.register(Orphans.path((String[])commandAliases.toArray(String[]::new)).handler(new FFACommands(plugin)));
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to register commands: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void registerEvents() {
        try {
            PluginManager manager = Bukkit.getPluginManager();
            manager.registerEvents((Listener)new ArenaListener(plugin), (Plugin)plugin);
            manager.registerEvents((Listener)new CommandRestrictionListener(plugin), (Plugin)plugin);
            manager.registerEvents((Listener)new EnderpearlListener(plugin), (Plugin)plugin);
            manager.registerEvents((Listener)new ItemDropListener(plugin), (Plugin)plugin);
            manager.registerEvents((Listener)plugin.getStarterItemListener(), (Plugin)plugin);
            manager.registerEvents((Listener)new SpectatorItemListener(plugin), (Plugin)plugin);
            manager.registerEvents((Listener)new VanishListener(plugin), (Plugin)plugin);
            manager.registerEvents((Listener)plugin.getBorderTeleportListener(), (Plugin)plugin);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to register event listeners: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<String> getOnlinePlayerNames() {
        try {
            return plugin.getServer().getOnlinePlayers().stream().map(player -> player.getName()).filter(name -> name != null && !name.isEmpty()).sorted().collect(Collectors.toList());
        } catch (Exception e) {
            plugin.getLogger().warning("Error getting online player names for tab completion: " + e.getMessage());
            return new ArrayList<String>();
        }
    }

    private static List<String> getArenaNames() {
        try {
            if (plugin.getArenaManager() == null) {
                return new ArrayList<String>();
            }
            return plugin.getArenaManager().getArenaNames().stream().filter(name -> name != null && !name.isEmpty()).sorted().collect(Collectors.toList());
        } catch (Exception e) {
            plugin.getLogger().warning("Error getting arena names for tab completion: " + e.getMessage());
            return new ArrayList<String>();
        }
    }

    private static List<String> getEnabledArenaNames() {
        try {
            if (plugin.getArenaManager() == null) {
                return new ArrayList<String>();
            }
            return plugin.getArenaManager().getArenaNames().stream().filter(name -> {
                try {
                    Arena arena = plugin.getArenaManager().getArena((String)name);
                    return arena != null && arena.isEnabled() && arena.isStarterEnabled();
                } catch (Exception e) {
                    return false;
                }
            }).sorted().collect(Collectors.toList());
        } catch (Exception e) {
            plugin.getLogger().warning("Error getting enabled arena names for tab completion: " + e.getMessage());
            return new ArrayList<String>();
        }
    }

    private static List<String> getRunningArenaNames() {
        try {
            if (plugin.getArenaManager() == null) {
                return new ArrayList<String>();
            }
            return plugin.getArenaManager().getArenaNames().stream().filter(name -> {
                try {
                    Arena arena = plugin.getArenaManager().getArena((String)name);
                    return arena != null && arena.isRunning();
                } catch (Exception e) {
                    return false;
                }
            }).sorted().collect(Collectors.toList());
        } catch (Exception e) {
            plugin.getLogger().warning("Error getting running arena names for tab completion: " + e.getMessage());
            return new ArrayList<String>();
        }
    }

    private static List<String> getLocationTypes() {
        return Arrays.asList("start-location", "exit-location", "border-center-location");
    }
}

