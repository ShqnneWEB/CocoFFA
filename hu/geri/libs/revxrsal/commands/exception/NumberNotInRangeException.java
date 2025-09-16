/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.exception.ThrowableFromCommand;
import org.jetbrains.annotations.NotNull;

@ThrowableFromCommand
public class NumberNotInRangeException
extends RuntimeException {
    @NotNull
    private final Number input;
    private final double minimum;
    private final double maximum;

    public NumberNotInRangeException(@NotNull Number input, double minimum, double maximum) {
        this.input = input;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @NotNull
    public Number input() {
        return this.input;
    }

    public double minimum() {
        return this.minimum;
    }

    public double maximum() {
        return this.maximum;
    }
}

