/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.hook;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.hook.CancelHandle;
import hu.geri.libs.revxrsal.commands.hook.Hook;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CommandExecutedHook<A extends CommandActor>
extends Hook {
    public void onExecuted(@NotNull ExecutableCommand<A> var1, @NotNull ExecutionContext<A> var2, @NotNull CancelHandle var3);
}

