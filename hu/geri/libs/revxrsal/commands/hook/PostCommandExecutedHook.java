/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.hook;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.hook.Hook;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface PostCommandExecutedHook<A extends CommandActor>
extends Hook {
    public void onPostExecuted(@NotNull ExecutableCommand<A> var1, @NotNull ExecutionContext<A> var2);
}

