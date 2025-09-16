/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.bukkit.exception;

import hu.geri.libs.revxrsal.commands.exception.InvalidValueException;
import org.jetbrains.annotations.NotNull;

public class MissingLocationParameterException
extends InvalidValueException {
    @NotNull
    private final MissingAxis missingAxis;

    public MissingLocationParameterException(@NotNull String input, @NotNull MissingAxis missingAxis) {
        super(input);
        this.missingAxis = missingAxis;
    }

    @NotNull
    public MissingAxis axis() {
        return this.missingAxis;
    }

    public static enum MissingAxis {
        X,
        Y,
        Z;

    }
}

