/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception.context;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.context.ExecutingFunctionContext;
import hu.geri.libs.revxrsal.commands.exception.context.ParsingLiteralContext;
import hu.geri.libs.revxrsal.commands.exception.context.ParsingParameterContext;
import hu.geri.libs.revxrsal.commands.exception.context.UnknownCommandContext;
import hu.geri.libs.revxrsal.commands.exception.context.UnknownParameterContext;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.node.LiteralNode;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ErrorContext<A extends CommandActor> {
    @NotNull
    public static <A extends CommandActor> ParsingLiteral<A> parsingLiteral(@NotNull ExecutionContext<A> context, @NotNull LiteralNode<A> node) {
        return new ParsingLiteralContext<A>(context, node);
    }

    @NotNull
    public static <A extends CommandActor> ParsingParameter<A> parsingParameter(@NotNull ExecutionContext<A> context, @NotNull ParameterNode<A, ?> node, @NotNull StringStream input) {
        return new ParsingParameterContext<A>(context, node, input);
    }

    @NotNull
    public static <A extends CommandActor> ExecutingFunction<A> executingFunction(@NotNull ExecutionContext<A> context) {
        return new ExecutingFunctionContext<A>(context);
    }

    @NotNull
    public static <A extends CommandActor> UnknownCommand<A> unknownCommand(@NotNull A actor) {
        return new UnknownCommandContext<A>(actor);
    }

    @NotNull
    public static <A extends CommandActor> UnknownParameter<A> unknownParameter(@NotNull ExecutionContext<A> context) {
        return new UnknownParameterContext<A>(context);
    }

    default public boolean hasExecutionContext() {
        return this.context() != null;
    }

    public ExecutionContext<A> context();

    @NotNull
    public A actor();

    @NotNull
    public Lamp<A> lamp();

    default public boolean isParsingLiteral() {
        return this instanceof ParsingLiteral;
    }

    default public boolean isParsingParameter() {
        return this instanceof ParsingParameter;
    }

    default public boolean isExecutingFunction() {
        return this instanceof ExecutingFunctionContext;
    }

    public static interface UnknownParameter<A extends CommandActor>
    extends ErrorContext<A> {
        @Override
        @NotNull
        public ExecutionContext<A> context();
    }

    public static interface UnknownCommand<A extends CommandActor>
    extends ErrorContext<A> {
        @Override
        @Contract(value="-> null")
        @Nullable
        default public ExecutionContext<A> context() {
            return null;
        }
    }

    public static interface ExecutingFunction<A extends CommandActor>
    extends ErrorContext<A> {
        @Override
        @NotNull
        public ExecutionContext<A> context();
    }

    public static interface ParsingParameter<A extends CommandActor>
    extends ErrorContext<A> {
        @NotNull
        public ParameterNode<A, ?> parameter();

        @Override
        @NotNull
        public ExecutionContext<A> context();
    }

    public static interface ParsingLiteral<A extends CommandActor>
    extends ErrorContext<A> {
        @NotNull
        public LiteralNode<A> literal();

        @Override
        @NotNull
        public ExecutionContext<A> context();
    }
}

