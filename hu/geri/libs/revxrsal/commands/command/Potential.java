/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.command;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.context.ErrorContext;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Potential<A extends CommandActor>
extends Comparable<Potential<A>> {
    public boolean successful();

    public boolean failed();

    @NotNull
    public ExecutionContext<A> context();

    @Nullable
    public Throwable error();

    @Nullable
    public ErrorContext<A> errorContext();

    public void handleException();

    public void execute();
}

