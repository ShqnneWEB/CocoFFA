/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception.context;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.context.ErrorContext;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

final class ParsingParameterContext<A extends CommandActor>
implements ErrorContext.ParsingParameter<A> {
    @NotNull
    private final ExecutionContext<A> context;
    @NotNull
    private final ParameterNode<A, ?> parameter;
    @NotNull
    private final StringStream input;

    ParsingParameterContext(@NotNull ExecutionContext<A> context, @NotNull ParameterNode<A, ?> parameter, @NotNull StringStream input) {
        this.context = context;
        this.parameter = parameter;
        this.input = input;
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

    @Override
    @NotNull
    public ParameterNode<A, ?> parameter() {
        return this.parameter;
    }

    @NotNull
    public StringStream input() {
        return this.input;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        ParsingParameterContext that = (ParsingParameterContext)obj;
        return Objects.equals(this.context, that.context) && Objects.equals(this.parameter, that.parameter) && Objects.equals(this.input, that.input);
    }

    public int hashCode() {
        return Objects.hash(this.context, this.parameter, this.input);
    }

    public String toString() {
        return "ParsingParameterContext[context=" + this.context + ", parameter=" + this.parameter + ", input=" + this.input + ']';
    }
}

