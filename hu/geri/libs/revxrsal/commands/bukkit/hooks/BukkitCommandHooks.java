/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.plugin.java.JavaPlugin
 */
package hu.geri.libs.revxrsal.commands.bukkit.hooks;

import hu.geri.libs.revxrsal.commands.bukkit.actor.ActorFactory;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.bukkit.annotation.FallbackPrefix;
import hu.geri.libs.revxrsal.commands.bukkit.hooks.LampCommandExecutor;
import hu.geri.libs.revxrsal.commands.bukkit.util.PluginCommands;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.hook.CancelHandle;
import hu.geri.libs.revxrsal.commands.hook.CommandRegisteredHook;
import hu.geri.libs.revxrsal.commands.hook.CommandUnregisteredHook;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class BukkitCommandHooks<A extends BukkitCommandActor>
implements CommandRegisteredHook<A>,
CommandUnregisteredHook<A> {
    private final Set<String> registeredRootNames = new HashSet<String>();
    private final JavaPlugin plugin;
    private final ActorFactory<A> actorFactory;
    private final String defaultFallbackPrefix;

    public BukkitCommandHooks(JavaPlugin plugin, ActorFactory<A> actorFactory, @NotNull String defaultFallbackPrefix) {
        this.plugin = plugin;
        this.actorFactory = actorFactory;
        this.defaultFallbackPrefix = defaultFallbackPrefix;
    }

    @Override
    public void onRegistered(@NotNull ExecutableCommand<A> command, @NotNull CancelHandle cancelHandle) {
        String name = command.firstNode().name();
        if (this.registeredRootNames.add(name)) {
            String fallbackPrefix = command.annotations().mapOr(FallbackPrefix.class, FallbackPrefix::value, this.defaultFallbackPrefix);
            PluginCommand cmd = PluginCommands.create(fallbackPrefix, command.firstNode().name(), this.plugin);
            LampCommandExecutor<A> executor = new LampCommandExecutor<A>(command.lamp(), this.actorFactory);
            cmd.setExecutor(executor);
            cmd.setTabCompleter(executor);
            if (cmd.getDescription().isEmpty() && command.description() != null) {
                cmd.setDescription(Objects.requireNonNull(command.description()));
            }
            if (cmd.getUsage().isEmpty()) {
                cmd.setUsage(command.usage());
            }
        }
    }

    @Override
    public void onUnregistered(@NotNull ExecutableCommand<A> command, @NotNull CancelHandle cancelHandle) {
        String label = command.firstNode().name();
        String fallbackPrefix = this.fallbackPrefix(command);
        PluginCommand cmd = Bukkit.getServer().getPluginCommand(fallbackPrefix + ':' + label);
        if (!command.lamp().registry().any(c -> c != command && c.firstNode().name().equals(label) && this.fallbackPrefix((ExecutableCommand<A>)c).equals(fallbackPrefix)) && cmd != null) {
            PluginCommands.unregister(cmd, this.plugin);
        }
        if (!command.lamp().registry().any(c -> c != command && c.firstNode().name().equals(label)) && (cmd = PluginCommands.getCommand(this.plugin, label)) != null) {
            PluginCommands.unregister(cmd, this.plugin);
        }
    }

    @NotNull
    private String fallbackPrefix(@NotNull ExecutableCommand<A> command) {
        return command.annotations().mapOr(FallbackPrefix.class, FallbackPrefix::value, this.defaultFallbackPrefix);
    }
}

