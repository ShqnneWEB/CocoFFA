/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.process;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import org.jetbrains.annotations.NotNull;

public interface CommandCondition<A extends CommandActor> {
    public void test(@NotNull ExecutionContext<A> var1);
}

