/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.node.BasicExecutionContext;
import hu.geri.libs.revxrsal.commands.node.MutableExecutionContext;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import org.jetbrains.annotations.NotNull;

final class BasicMutableExecutionContext<A extends CommandActor>
extends BasicExecutionContext<A>
implements MutableExecutionContext<A> {
    public BasicMutableExecutionContext(ExecutableCommand<A> command, StringStream input, A actor) {
        super(command, input, actor);
    }

    @Override
    public void addResolvedArgument(@NotNull String name, Object result) {
        Object old = this.resolvedArguments.put(name, result);
        if (old != null) {
            throw new IllegalArgumentException("A parameter with name '" + name + "' already exists!");
        }
    }

    @Override
    public void clearResolvedArguments() {
        this.resolvedArguments.clear();
    }
}

