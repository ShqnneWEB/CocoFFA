/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.context.ErrorContext;
import org.jetbrains.annotations.NotNull;

public interface SelfHandledException<A extends CommandActor> {
    public void handle(@NotNull ErrorContext<A> var1);
}

