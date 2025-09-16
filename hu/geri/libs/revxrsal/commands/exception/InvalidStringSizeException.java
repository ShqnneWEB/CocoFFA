/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.exception.ThrowableFromCommand;
import org.jetbrains.annotations.NotNull;

@ThrowableFromCommand
public class InvalidStringSizeException
extends RuntimeException {
    private final int minimum;
    private final int maximum;
    private final String input;

    public InvalidStringSizeException(int minimum, int maximum, @NotNull String input) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.input = input;
    }

    public int minimum() {
        return this.minimum;
    }

    public int maximum() {
        return this.maximum;
    }

    @NotNull
    public String input() {
        return this.input;
    }
}

