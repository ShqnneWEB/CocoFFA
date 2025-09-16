/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.exception.ThrowableFromCommand;
import org.jetbrains.annotations.NotNull;

@ThrowableFromCommand
public abstract class InvalidValueException
extends RuntimeException {
    @NotNull
    private final String input;

    public InvalidValueException(@NotNull String input) {
        this.input = input;
    }

    @NotNull
    public String input() {
        return this.input;
    }
}

