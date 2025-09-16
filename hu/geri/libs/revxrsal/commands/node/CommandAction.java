/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;

@FunctionalInterface
public interface CommandAction<A extends CommandActor> {
    public void execute(ExecutionContext<A> var1);
}

