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

final class UnknownCommandContext<A extends CommandActor>
implements ErrorContext.UnknownCommand<A> {
    @NotNull
    private final A actor;

    UnknownCommandContext(@NotNull A actor) {
        this.actor = actor;
    }

    @Override
    public boolean hasExecutionContext() {
        return false;
    }

    @Override
    public ExecutionContext<A> context() {
        return null;
    }

    @Override
    @NotNull
    public A actor() {
        return this.actor;
    }

    @Override
    @NotNull
    public Lamp<A> lamp() {
        return this.actor.lamp();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        UnknownCommandContext that = (UnknownCommandContext)obj;
        return Objects.equals(this.actor, that.actor);
    }

    public int hashCode() {
        return Objects.hash(this.actor);
    }

    public String toString() {
        return "UnknownCommandContext[actor=" + this.actor + ']';
    }
}

