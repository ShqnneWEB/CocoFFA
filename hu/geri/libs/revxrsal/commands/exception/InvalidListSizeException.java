/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.exception.ThrowableFromCommand;
import java.util.List;
import org.jetbrains.annotations.NotNull;

@ThrowableFromCommand
public class InvalidListSizeException
extends RuntimeException {
    private final int minimum;
    private final int maximum;
    private final int inputSize;
    @NotNull
    private final List<Object> items;

    public InvalidListSizeException(int minimum, int maximum, int inputSize, @NotNull List<Object> items) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.inputSize = inputSize;
        this.items = items;
    }

    public int minimum() {
        return this.minimum;
    }

    public int maximum() {
        return this.maximum;
    }

    public int inputSize() {
        return this.inputSize;
    }

    @NotNull
    public <T> List<T> items() {
        return this.items;
    }
}

