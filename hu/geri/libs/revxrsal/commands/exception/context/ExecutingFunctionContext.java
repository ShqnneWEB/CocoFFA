/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception.context;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.context.ErrorContext;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

final class ExecutingFunctionContext<A extends CommandActor>
implements ErrorContext.ExecutingFunction<A> {
    private final ExecutionContext<A> context;

    ExecutingFunctionContext(ExecutionContext<A> context) {
        this.context = context;
    }

    @Override
    @NotNull
    public A actor() {
        return this.context.actor();
    }

    @Override
    @NotNull
    public Lamp<A> lamp() {
        return this.context.lamp();
    }

    @Override
    public ExecutionContext<A> context() {
        return this.context;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        ExecutingFunctionContext that = (ExecutingFunctionContext)obj;
        return Objects.equals(this.context, that.context);
    }

    public int hashCode() {
        return Objects.hash(this.context);
    }

    public String toString() {
        return "ExecutingFunctionContext[context=" + this.context + ']';
    }
}

