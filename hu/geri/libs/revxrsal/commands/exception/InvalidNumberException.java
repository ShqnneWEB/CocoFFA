/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.exception.InvalidValueException;
import org.jetbrains.annotations.NotNull;

public abstract class InvalidNumberException
extends InvalidValueException {
    public InvalidNumberException(@NotNull String input) {
        super(input);
    }
}

