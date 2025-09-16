/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.hook;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.hook.CancelHandle;
import hu.geri.libs.revxrsal.commands.hook.CommandExecutedHook;
import hu.geri.libs.revxrsal.commands.hook.CommandRegisteredHook;
import hu.geri.libs.revxrsal.commands.hook.CommandUnregisteredHook;
import hu.geri.libs.revxrsal.commands.hook.Hook;
import hu.geri.libs.revxrsal.commands.hook.PostCommandExecutedHook;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.util.Collections;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public final class Hooks<A extends CommandActor> {
    private final @Unmodifiable List<Hook> hooks;

    private Hooks(Builder<A> builder) {
        this.hooks = Collections.copyList(((Builder)builder).hooks);
    }

    @Contract(value="-> new", pure=true)
    @NotNull
    public static <A extends CommandActor> Builder<A> builder() {
        return new Builder();
    }

    @NotNull
    private static CancelHandle newCancelHandle() {
        return new BasicCancelHandle();
    }

    @ApiStatus.Internal
    public boolean onCommandRegistered(@NotNull ExecutableCommand<A> command) {
        CancelHandle cancelHandle = Hooks.newCancelHandle();
        for (Hook hook : this.hooks) {
            if (!(hook instanceof CommandRegisteredHook)) continue;
            CommandRegisteredHook registeredHook = (CommandRegisteredHook)hook;
            registeredHook.onRegistered(command, cancelHandle);
        }
        return !cancelHandle.wasCancelled();
    }

    @ApiStatus.Internal
    public boolean onCommandUnregistered(@NotNull ExecutableCommand<A> command) {
        CancelHandle cancelHandle = Hooks.newCancelHandle();
        for (Hook hook : this.hooks) {
            if (!(hook instanceof CommandUnregisteredHook)) continue;
            CommandUnregisteredHook unregisteredHook = (CommandUnregisteredHook)hook;
            unregisteredHook.onUnregistered(command, cancelHandle);
        }
        return !cancelHandle.wasCancelled();
    }

    @ApiStatus.Internal
    public boolean onCommandExecuted(@NotNull ExecutableCommand<A> command, @NotNull ExecutionContext<A> context) {
        CancelHandle cancelHandle = Hooks.newCancelHandle();
        for (Hook hook : this.hooks) {
            if (!(hook instanceof CommandExecutedHook)) continue;
            CommandExecutedHook executedHook = (CommandExecutedHook)hook;
            executedHook.onExecuted(command, context, cancelHandle);
        }
        return !cancelHandle.wasCancelled();
    }

    @ApiStatus.Internal
    public void onPostCommandExecuted(@NotNull ExecutableCommand<A> command, @NotNull ExecutionContext<A> context) {
        for (Hook hook : this.hooks) {
            if (!(hook instanceof PostCommandExecutedHook)) continue;
            PostCommandExecutedHook executedHook = (PostCommandExecutedHook)hook;
            executedHook.onPostExecuted(command, context);
        }
    }

    private static final class BasicCancelHandle
    implements CancelHandle {
        private boolean cancelled = false;

        private BasicCancelHandle() {
        }

        @Override
        public boolean wasCancelled() {
            return this.cancelled;
        }

        @Override
        public void cancel() {
            this.cancelled = true;
        }
    }

    public static class Builder<A extends CommandActor> {
        private final List<Hook> hooks = new ArrayList<Hook>();

        @NotNull
        public Builder<A> onCommandExecuted(@NotNull CommandExecutedHook<? super A> hook) {
            return this.hook(hook);
        }

        @NotNull
        public Builder<A> onPostCommandExecuted(@NotNull PostCommandExecutedHook<? super A> hook) {
            return this.hook(hook);
        }

        @NotNull
        public Builder<A> onCommandRegistered(@NotNull CommandRegisteredHook<? super A> hook) {
            return this.hook(hook);
        }

        @NotNull
        public Builder<A> onCommandUnregistered(@NotNull CommandUnregisteredHook<? super A> hook) {
            return this.hook(hook);
        }

        @NotNull
        private Builder<A> hook(@NotNull Hook hook) {
            Preconditions.notNull(hook, "hook");
            this.hooks.add(hook);
            return this;
        }

        @Contract(value="-> new", pure=true)
        @NotNull
        public Hooks<A> build() {
            return new Hooks(this);
        }
    }
}

