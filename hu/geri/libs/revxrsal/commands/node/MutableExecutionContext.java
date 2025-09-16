/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import org.jetbrains.annotations.NotNull;

public interface MutableExecutionContext<A extends CommandActor>
extends ExecutionContext<A> {
    public void addResolvedArgument(@NotNull String var1, Object var2);

    public void clearResolvedArguments();
}

