/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.exception.InvalidNumberException;
import org.jetbrains.annotations.NotNull;

public class InvalidDecimalException
extends InvalidNumberException {
    public InvalidDecimalException(@NotNull String input) {
        super(input);
    }
}

