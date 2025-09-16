/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.exception.ThrowableFromCommand;
import org.jetbrains.annotations.NotNull;

@ThrowableFromCommand
public class CommandInvocationException
extends RuntimeException {
    @NotNull
    private final Throwable cause;

    public CommandInvocationException(@NotNull Throwable cause) {
        this.cause = cause;
    }

    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }

    @NotNull
    public Throwable cause() {
        return this.cause;
    }
}

