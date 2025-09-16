/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.ThrowableFromCommand;
import org.jetbrains.annotations.NotNull;

@ThrowableFromCommand
public abstract class SendableException
extends RuntimeException {
    public SendableException() {
        this("");
    }

    public SendableException(String message) {
        super(message);
    }

    public abstract void sendTo(@NotNull CommandActor var1);
}

