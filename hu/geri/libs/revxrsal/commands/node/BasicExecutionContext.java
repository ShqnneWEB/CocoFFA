/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import hu.geri.libs.revxrsal.commands.util.Classes;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

class BasicExecutionContext<A extends CommandActor>
implements ExecutionContext<A> {
    protected final ExecutableCommand<A> command;
    protected final StringStream input;
    protected final A actor;
    protected final Map<String, Object> resolvedArguments = new LinkedHashMap<String, Object>();

    public BasicExecutionContext(ExecutableCommand<A> command, StringStream input, A actor) {
        this.command = command;
        this.input = input;
        this.actor = actor;
    }

    @Override
    @NotNull
    public A actor() {
        return this.actor;
    }

    @Override
    @NotNull
    public Lamp<A> lamp() {
        return this.command.lamp();
    }

    @Override
    @NotNull
    public ExecutableCommand<A> command() {
        return this.command;
    }

    @Override
    @NotNull
    public @UnmodifiableView Map<String, Object> resolvedArguments() {
        return this.resolvedArguments;
    }

    @Override
    @NotNull
    public StringStream input() {
        return this.input;
    }

    @Override
    @Nullable
    public <T> T getResolvedArgumentOrNull(@NotNull Class<T> argumentType) {
        Preconditions.notNull(argumentType, "argument type");
        argumentType = Classes.wrap(argumentType);
        for (Object value : this.resolvedArguments.values()) {
            if (value == null || !argumentType.isAssignableFrom(value.getClass())) continue;
            return (T)value;
        }
        return null;
    }

    @Override
    @Nullable
    public <T> T getResolvedArgumentOrNull(@NotNull String argumentName) {
        Preconditions.notNull(argumentName, "argument name");
        return (T)this.resolvedArguments.get(argumentName);
    }
}

