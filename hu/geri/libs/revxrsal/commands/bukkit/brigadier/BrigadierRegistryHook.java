/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.java.JavaPlugin
 */
package hu.geri.libs.revxrsal.commands.bukkit.brigadier;

import hu.geri.libs.revxrsal.commands.brigadier.types.ArgumentTypes;
import hu.geri.libs.revxrsal.commands.bukkit.actor.ActorFactory;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.bukkit.brigadier.BukkitBrigadierBridge;
import hu.geri.libs.revxrsal.commands.bukkit.brigadier.ByPaperEvents;
import hu.geri.libs.revxrsal.commands.bukkit.brigadier.ByPaperLifecycle;
import hu.geri.libs.revxrsal.commands.bukkit.brigadier.ByReflection;
import hu.geri.libs.revxrsal.commands.bukkit.util.BukkitVersion;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.hook.CancelHandle;
import hu.geri.libs.revxrsal.commands.hook.CommandRegisteredHook;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class BrigadierRegistryHook<A extends BukkitCommandActor>
implements CommandRegisteredHook<A> {
    private final ActorFactory<A> actorFactory;
    private final ArgumentTypes<A> argumentTypes;
    private final BukkitBrigadierBridge<A> bridge;
    private final JavaPlugin plugin;

    public BrigadierRegistryHook(ArgumentTypes<A> argumentTypes, ActorFactory<A> actorFactory, JavaPlugin plugin) {
        this.actorFactory = actorFactory;
        this.argumentTypes = argumentTypes;
        this.plugin = plugin;
        this.bridge = this.createBridge();
    }

    private BukkitBrigadierBridge<A> createBridge() {
        if (BukkitVersion.isPaper()) {
            if (BukkitVersion.supports(1, 20, 6)) {
                return new ByPaperLifecycle<A>(this.plugin, this.argumentTypes, this.actorFactory);
            }
            if (BukkitVersion.supports(1, 19)) {
                return new ByPaperEvents<A>(this.plugin, this.argumentTypes, this.actorFactory);
            }
        }
        return new ByReflection<A>(this.plugin, this.argumentTypes, this.actorFactory);
    }

    @Override
    public void onRegistered(@NotNull ExecutableCommand<A> command, @NotNull CancelHandle cancelHandle) {
        this.bridge.register(command);
    }
}

