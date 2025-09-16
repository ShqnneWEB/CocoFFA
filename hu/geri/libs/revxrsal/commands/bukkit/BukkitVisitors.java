/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package hu.geri.libs.revxrsal.commands.bukkit;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.LampBuilderVisitor;
import hu.geri.libs.revxrsal.commands.brigadier.types.ArgumentTypes;
import hu.geri.libs.revxrsal.commands.bukkit.actor.ActorFactory;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.bukkit.brigadier.BrigadierRegistryHook;
import hu.geri.libs.revxrsal.commands.bukkit.brigadier.BukkitArgumentTypes;
import hu.geri.libs.revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import hu.geri.libs.revxrsal.commands.bukkit.hooks.BukkitCommandHooks;
import hu.geri.libs.revxrsal.commands.bukkit.listener.AsyncPaperTabListener;
import hu.geri.libs.revxrsal.commands.bukkit.parameters.EntityParameterType;
import hu.geri.libs.revxrsal.commands.bukkit.parameters.EntitySelectorParameterTypeFactory;
import hu.geri.libs.revxrsal.commands.bukkit.parameters.LocationParameterType;
import hu.geri.libs.revxrsal.commands.bukkit.parameters.OfflinePlayerParameterType;
import hu.geri.libs.revxrsal.commands.bukkit.parameters.PlayerParameterType;
import hu.geri.libs.revxrsal.commands.bukkit.parameters.WorldParameterType;
import hu.geri.libs.revxrsal.commands.bukkit.sender.BukkitPermissionFactory;
import hu.geri.libs.revxrsal.commands.bukkit.sender.BukkitSenderResolver;
import hu.geri.libs.revxrsal.commands.bukkit.util.BukkitUtils;
import hu.geri.libs.revxrsal.commands.bukkit.util.BukkitVersion;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class BukkitVisitors {
    @NotNull
    public static <A extends BukkitCommandActor> LampBuilderVisitor<A> legacyColorCodes() {
        return builder -> builder.defaultMessageSender((actor, message) -> actor.sendRawMessage(BukkitUtils.legacyColorize(message))).defaultErrorSender((actor, message) -> actor.sendRawMessage(BukkitUtils.legacyColorize("&c" + message)));
    }

    @NotNull
    public static <A extends BukkitCommandActor> LampBuilderVisitor<A> bukkitExceptionHandler() {
        return builder -> builder.exceptionHandler(new BukkitExceptionHandler());
    }

    @NotNull
    public static <A extends BukkitCommandActor> LampBuilderVisitor<A> bukkitSenderResolver() {
        return builder -> builder.senderResolver(new BukkitSenderResolver());
    }

    @NotNull
    public static <A extends BukkitCommandActor> LampBuilderVisitor<A> bukkitParameterTypes() {
        return BukkitVisitors.bukkitParameterTypes(BukkitVersion.isBrigadierSupported());
    }

    @NotNull
    public static <A extends BukkitCommandActor> LampBuilderVisitor<A> bukkitParameterTypes(boolean brigadierEnabled) {
        return builder -> {
            builder.parameterTypes().addParameterTypeLast(Player.class, new PlayerParameterType(brigadierEnabled)).addParameterTypeLast(OfflinePlayer.class, new OfflinePlayerParameterType(brigadierEnabled)).addParameterTypeLast(World.class, new WorldParameterType()).addParameterTypeLast(Location.class, new LocationParameterType()).addParameterTypeFactoryLast(new EntitySelectorParameterTypeFactory());
            if (BukkitVersion.isBrigadierSupported()) {
                builder.parameterTypes().addParameterTypeLast(Entity.class, new EntityParameterType());
            }
        };
    }

    @NotNull
    public static LampBuilderVisitor<BukkitCommandActor> registrationHooks(@NotNull JavaPlugin plugin) {
        return BukkitVisitors.registrationHooks(plugin, ActorFactory.defaultFactory((Plugin)plugin, Optional.empty()));
    }

    @NotNull
    public static <A extends BukkitCommandActor> LampBuilderVisitor<A> registrationHooks(@NotNull JavaPlugin plugin, @NotNull ActorFactory<A> actorFactory) {
        return BukkitVisitors.registrationHooks(plugin, actorFactory, plugin.getName());
    }

    @NotNull
    public static <A extends BukkitCommandActor> LampBuilderVisitor<A> registrationHooks(@NotNull JavaPlugin plugin, @NotNull ActorFactory<A> actorFactory, @NotNull String defaultFallbackPrefix) {
        BukkitCommandHooks hooks = new BukkitCommandHooks(plugin, actorFactory, defaultFallbackPrefix);
        return builder -> builder.hooks().onCommandRegistered(hooks);
    }

    @NotNull
    public static <A extends BukkitCommandActor> LampBuilderVisitor<A> pluginContextParameters(JavaPlugin plugin) {
        return builder -> {
            builder.parameterTypes().addContextParameterLast(Plugin.class, (parameter, context) -> plugin);
            builder.parameterTypes().addContextParameterLast(plugin.getClass(), (parameter, context) -> plugin);
            builder.dependency(Plugin.class, plugin);
            builder.dependency(plugin.getClass(), plugin);
        };
    }

    @NotNull
    public static <A extends BukkitCommandActor> LampBuilderVisitor<A> bukkitPermissions() {
        return builder -> builder.permissionFactory(BukkitPermissionFactory.INSTANCE);
    }

    @NotNull
    public static LampBuilderVisitor<BukkitCommandActor> brigadier(@NotNull JavaPlugin plugin) {
        ArgumentTypes.Builder builder = BukkitArgumentTypes.builder();
        return BukkitVisitors.brigadier(plugin, builder.build(), ActorFactory.defaultFactory((Plugin)plugin, Optional.empty()));
    }

    @NotNull
    public static LampBuilderVisitor<BukkitCommandActor> brigadier(@NotNull JavaPlugin plugin, @NotNull ArgumentTypes<? super BukkitCommandActor> argumentTypes) {
        return BukkitVisitors.brigadier(plugin, argumentTypes, ActorFactory.defaultFactory((Plugin)plugin, Optional.empty()));
    }

    @NotNull
    public static <A extends BukkitCommandActor> LampBuilderVisitor<A> brigadier(@NotNull JavaPlugin plugin, @NotNull ArgumentTypes<? super A> argumentTypes, @NotNull ActorFactory<A> actorFactory) {
        if (BukkitVersion.isBrigadierSupported()) {
            return builder -> builder.hooks().onCommandRegistered(new BrigadierRegistryHook(argumentTypes, actorFactory, plugin));
        }
        return LampBuilderVisitor.nothing();
    }

    @NotNull
    public static <A extends BukkitCommandActor> LampBuilderVisitor<A> asyncTabCompletion(final @NotNull JavaPlugin plugin, final @NotNull ActorFactory<A> actorFactory) {
        if (BukkitVersion.supportsAsyncCompletion()) {
            return new LampBuilderVisitor<A>(){
                private boolean registered = false;

                @Override
                public void visit(@NotNull Lamp.Builder<A> builder) {
                    builder.hooks().onCommandRegistered((command, cancelHandle) -> {
                        if (this.registered) {
                            return;
                        }
                        this.registered = true;
                        Bukkit.getPluginManager().registerEvents(new AsyncPaperTabListener(command.lamp(), actorFactory), (Plugin)plugin);
                    });
                }
            };
        }
        return LampBuilderVisitor.nothing();
    }
}

