/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.node.BasicExecutionContext;
import hu.geri.libs.revxrsal.commands.node.BasicMutableExecutionContext;
import hu.geri.libs.revxrsal.commands.node.MutableExecutionContext;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

public interface ExecutionContext<A extends CommandActor> {
    @Contract(value="_,_,_ -> new")
    @NotNull
    public static <A extends CommandActor> MutableExecutionContext<A> createMutable(@NotNull ExecutableCommand<A> command, @NotNull A actor, @NotNull StringStream input) {
        Preconditions.notNull(command, "command");
        Preconditions.notNull(actor, "actor");
        Preconditions.notNull(input, "input");
        return new BasicMutableExecutionContext<A>(command, input, actor);
    }

    @Contract(value="_,_,_ -> new")
    @NotNull
    public static <A extends CommandActor> ExecutionContext<A> create(@NotNull ExecutableCommand<A> command, @NotNull A actor, @NotNull StringStream input) {
        Preconditions.notNull(command, "command");
        Preconditions.notNull(actor, "actor");
        Preconditions.notNull(input, "input");
        return new BasicExecutionContext<A>(command, input, actor);
    }

    @NotNull
    public A actor();

    @NotNull
    public Lamp<A> lamp();

    @NotNull
    public ExecutableCommand<A> command();

    @NotNull
    public @UnmodifiableView Map<String, Object> resolvedArguments();

    @NotNull
    public StringStream input();

    @Nullable
    public <T> T getResolvedArgumentOrNull(@NotNull String var1);

    @Nullable
    public <T> T getResolvedArgumentOrNull(@NotNull Class<T> var1);

    @NotNull
    default public <T> T getResolvedArgument(@NotNull String argumentName) {
        T argument = this.getResolvedArgumentOrNull(argumentName);
        if (argument == null) {
            throw new IllegalArgumentException("Argument '" + argumentName + "' not found (or hasn't been resolved yet). Possible argument names: " + this.resolvedArguments().keySet());
        }
        return argument;
    }

    @NotNull
    default public <T> T getResolvedArgument(@NotNull Class<T> argumentType) {
        T argument = this.getResolvedArgumentOrNull(argumentType);
        if (argument == null) {
            List types = this.resolvedArguments().values().stream().filter(Objects::nonNull).map(Object::getClass).collect(Collectors.toList());
            throw new IllegalArgumentException("Couldn't find an argument that matches the type " + argumentType + ". Available types: " + types);
        }
        return argument;
    }
}

