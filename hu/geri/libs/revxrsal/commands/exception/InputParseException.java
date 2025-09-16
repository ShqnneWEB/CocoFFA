/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.exception.ThrowableFromCommand;
import org.jetbrains.annotations.NotNull;

@ThrowableFromCommand
public final class InputParseException
extends RuntimeException {
    @NotNull
    private final Cause cause;

    public InputParseException(@NotNull Cause cause) {
        this.cause = cause;
    }

    @NotNull
    public Cause cause() {
        return this.cause;
    }

    public static enum Cause {
        INVALID_ESCAPE_CHARACTER,
        UNCLOSED_QUOTE,
        EXPECTED_WHITESPACE;

    }
}

