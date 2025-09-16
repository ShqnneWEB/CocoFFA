/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandMap
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.command.SimpleCommandMap
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.jetbrains.annotations.CheckReturnValue
 */
package hu.geri.libs.revxrsal.commands.bukkit.util;

import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class PluginCommands {
    private static final Constructor<PluginCommand> COMMAND_CONSTRUCTOR;
    @Nullable
    private static final Field KNOWN_COMMANDS;
    @Nullable
    private static final MethodHandle GET_PLUGIN_META;
    private static final CommandMap COMMAND_MAP;

    private PluginCommands() {
        Preconditions.cannotInstantiate(PluginCommands.class);
    }

    @CheckReturnValue
    @NotNull
    public static PluginCommand create(String name, @NotNull JavaPlugin plugin) {
        return PluginCommands.create(plugin.getName(), name, plugin);
    }

    @CheckReturnValue
    @NotNull
    public static PluginCommand create(String fallbackPrefix, String name, @NotNull JavaPlugin plugin) {
        PluginCommand command = PluginCommands.getCommand(plugin, name);
        if (command != null) {
            return command;
        }
        command = COMMAND_CONSTRUCTOR.newInstance(name, plugin);
        COMMAND_MAP.register(fallbackPrefix, (Command)command);
        return command;
    }

    public static void unregister(@NotNull PluginCommand command, @NotNull JavaPlugin owningPlugin) {
        Command rawAlias;
        command.unregister(COMMAND_MAP);
        Map<String, Command> knownCommands = PluginCommands.getKnownCommands();
        if (knownCommands != null && (rawAlias = knownCommands.get(command.getName())) instanceof PluginCommand && ((PluginCommand)rawAlias).getPlugin() == owningPlugin) {
            knownCommands.remove(command.getName());
        }
    }

    @Nullable
    private static Map<String, Command> getKnownCommands() {
        if (KNOWN_COMMANDS != null) {
            return (Map)KNOWN_COMMANDS.get(COMMAND_MAP);
        }
        return null;
    }

    @Nullable
    public static PluginCommand getCommand(@NotNull JavaPlugin plugin, @NotNull String name) {
        @Nullable Object meta = PluginCommands.getPluginMetaOrNull(plugin);
        if (meta == null || meta instanceof PluginDescriptionFile) {
            return plugin.getCommand(name);
        }
        return null;
    }

    @Nullable
    private static Object getPluginMetaOrNull(@NotNull JavaPlugin plugin) {
        if (GET_PLUGIN_META == null) {
            return null;
        }
        return GET_PLUGIN_META.invoke(plugin);
    }

    static {
        CommandMap commandMap;
        Constructor ctr;
        Field knownCommands = null;
        MethodHandle getPluginMeta = null;
        try {
            getPluginMeta = MethodHandles.lookup().unreflect(JavaPlugin.class.getDeclaredMethod("getPluginMeta", new Class[0]));
        } catch (Exception exception) {
            // empty catch block
        }
        try {
            ctr = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            ctr.setAccessible(true);
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap)commandMapField.get(Bukkit.getServer());
            if (commandMap instanceof SimpleCommandMap) {
                knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
                knownCommands.setAccessible(true);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new IllegalStateException("Unable to access PluginCommand(String, Plugin) construtor!");
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            throw new IllegalStateException("Unable to access Bukkit.getServer()#commandMap!");
        }
        COMMAND_CONSTRUCTOR = ctr;
        COMMAND_MAP = commandMap;
        KNOWN_COMMANDS = knownCommands;
        GET_PLUGIN_META = getPluginMeta;
    }
}

