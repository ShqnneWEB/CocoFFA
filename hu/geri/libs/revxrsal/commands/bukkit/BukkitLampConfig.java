/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package hu.geri.libs.revxrsal.commands.bukkit;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.LampBuilderVisitor;
import hu.geri.libs.revxrsal.commands.brigadier.types.ArgumentTypes;
import hu.geri.libs.revxrsal.commands.bukkit.BukkitVisitors;
import hu.geri.libs.revxrsal.commands.bukkit.actor.ActorFactory;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.bukkit.brigadier.BukkitArgumentTypes;
import hu.geri.libs.revxrsal.commands.bukkit.util.BukkitVersion;
import hu.geri.libs.revxrsal.commands.process.MessageSender;
import hu.geri.libs.revxrsal.commands.util.Lazy;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BukkitLampConfig<A extends BukkitCommandActor>
implements LampBuilderVisitor<A> {
    private final ActorFactory<A> actorFactory;
    private final Supplier<ArgumentTypes<A>> argumentTypes;
    private final JavaPlugin plugin;
    private final String fallbackPrefix;
    private final boolean disableBrigadier;
    private final boolean disableAsyncCompletion;

    public static <A extends BukkitCommandActor> Builder<A> builder(@NotNull JavaPlugin plugin) {
        Preconditions.notNull(plugin, "plugin");
        return new Builder(plugin);
    }

    public static BukkitLampConfig<BukkitCommandActor> createDefault(@NotNull JavaPlugin plugin) {
        Preconditions.notNull(plugin, "plugin");
        return new BukkitLampConfig<BukkitCommandActor>(ActorFactory.defaultFactory((Plugin)plugin, Optional.empty()), () -> BukkitArgumentTypes.builder().build(), plugin, plugin.getName(), false, false);
    }

    @Override
    public void visit(@NotNull Lamp.Builder<A> builder) {
        builder.accept(BukkitVisitors.legacyColorCodes()).accept(BukkitVisitors.bukkitSenderResolver()).accept(BukkitVisitors.bukkitParameterTypes(!this.disableBrigadier)).accept(BukkitVisitors.bukkitExceptionHandler()).accept(BukkitVisitors.bukkitPermissions()).accept(BukkitVisitors.registrationHooks(this.plugin, this.actorFactory, this.fallbackPrefix)).accept(BukkitVisitors.pluginContextParameters(this.plugin));
        if (!this.disableAsyncCompletion) {
            builder.accept(BukkitVisitors.asyncTabCompletion(this.plugin, this.actorFactory));
        }
        if (BukkitVersion.isBrigadierSupported() && !this.disableBrigadier) {
            builder.accept(BukkitVisitors.brigadier(this.plugin, this.argumentTypes.get(), this.actorFactory));
        }
    }

    public BukkitLampConfig(ActorFactory<A> actorFactory, Supplier<ArgumentTypes<A>> argumentTypes, JavaPlugin plugin, String fallbackPrefix, boolean disableBrigadier, boolean disableAsyncCompletion) {
        this.actorFactory = actorFactory;
        this.argumentTypes = argumentTypes;
        this.plugin = plugin;
        this.fallbackPrefix = fallbackPrefix;
        this.disableBrigadier = disableBrigadier;
        this.disableAsyncCompletion = disableAsyncCompletion;
    }

    public static class Builder<A extends BukkitCommandActor> {
        private final Supplier<ArgumentTypes.Builder<A>> argumentTypes = Lazy.of(() -> BukkitArgumentTypes.builder());
        @NotNull
        private final JavaPlugin plugin;
        private ActorFactory<A> actorFactory;
        private boolean disableBrigadier;
        private String fallbackPrefix;
        private Optional<BukkitAudiences> audiences;
        @Nullable
        private MessageSender<A, ComponentLike> messageSender;
        private boolean disableAsyncCompletion = true;

        Builder(@NotNull JavaPlugin plugin) {
            this.plugin = plugin;
            this.fallbackPrefix = plugin.getName();
        }

        @NotNull
        public Builder<A> actorFactory(@NotNull ActorFactory<A> actorFactory) {
            this.actorFactory = actorFactory;
            return this;
        }

        @NotNull
        public ArgumentTypes.Builder<A> argumentTypes() {
            return this.argumentTypes.get();
        }

        @NotNull
        public Builder<A> argumentTypes(@NotNull Consumer<ArgumentTypes.Builder<A>> consumer) {
            consumer.accept(this.argumentTypes.get());
            return this;
        }

        @NotNull
        public Builder<A> disableBrigadier() {
            return this.disableBrigadier(true);
        }

        @NotNull
        public Builder<A> disableAsyncCompletion() {
            return this.disableAsyncCompletion(true);
        }

        @NotNull
        public Builder<A> enableAsyncCompletion() {
            return this.disableAsyncCompletion(false);
        }

        @NotNull
        public Builder<A> disableBrigadier(boolean disabled) {
            this.disableBrigadier = disabled;
            return this;
        }

        @NotNull
        public Builder<A> disableAsyncCompletion(boolean disabled) {
            this.disableAsyncCompletion = disabled;
            return this;
        }

        @NotNull
        public Builder<A> fallbackPrefix(String fallbackPrefix) {
            this.fallbackPrefix = fallbackPrefix;
            return this;
        }

        @NotNull
        public Builder<A> audiences(@NotNull BukkitAudiences audiences) {
            this.audiences = Optional.of(audiences);
            return this;
        }

        @NotNull
        public Builder<A> messageSender(@Nullable MessageSender<? super A, ComponentLike> messageSender) {
            this.messageSender = messageSender;
            return this;
        }

        @Contract(value="-> new")
        @NotNull
        public BukkitLampConfig<A> build() {
            this.actorFactory = (ActorFactory)ActorFactory.defaultFactory(this.plugin, this.audiences, this.messageSender);
            return new BukkitLampConfig<A>(this.actorFactory, Lazy.of(() -> this.argumentTypes.get().build()), this.plugin, this.fallbackPrefix, this.disableBrigadier, this.disableAsyncCompletion);
        }
    }
}

