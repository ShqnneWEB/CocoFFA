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

final class UnknownParameterContext<A extends CommandActor>
implements ErrorContext.UnknownParameter<A> {
    @NotNull
    private final ExecutionContext<A> context;

    UnknownParameterContext(@NotNull ExecutionContext<A> context) {
        this.context = context;
    }

    @Override
    public boolean hasExecutionContext() {
        return true;
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
    @NotNull
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
        UnknownParameterContext that = (UnknownParameterContext)obj;
        return Objects.equals(this.context, that.context);
    }

    public int hashCode() {
        return Objects.hash(this.context);
    }

    public String toString() {
        return "UnknownParameterContext[context=" + this.context + ']';
    }
}

