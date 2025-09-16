/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.context.ErrorContext;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CommandExceptionHandler<A extends CommandActor> {
    public void handleException(@NotNull Throwable var1, @NotNull ErrorContext<A> var2);
}

