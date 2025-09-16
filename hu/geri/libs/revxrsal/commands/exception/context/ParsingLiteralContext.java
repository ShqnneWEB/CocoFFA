/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception.context;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.context.ErrorContext;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.node.LiteralNode;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

final class ParsingLiteralContext<A extends CommandActor>
implements ErrorContext.ParsingLiteral<A> {
    @NotNull
    private final ExecutionContext<A> context;
    @NotNull
    private final LiteralNode<A> literal;

    ParsingLiteralContext(@NotNull ExecutionContext<A> context, @NotNull LiteralNode<A> literal) {
        this.context = context;
        this.literal = literal;
    }

    @Override
    @NotNull
    public A actor() {
        return this.context().actor();
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
    public LiteralNode<A> literal() {
        return this.literal;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        ParsingLiteralContext that = (ParsingLiteralContext)obj;
        return Objects.equals(this.context, that.context) && Objects.equals(this.literal, that.literal);
    }

    public int hashCode() {
        return Objects.hash(this.context, this.literal);
    }

    public String toString() {
        return "ParsingLiteralContext[context=" + this.context + ", literal=" + this.literal + ']';
    }
}

