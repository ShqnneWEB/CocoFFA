/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.exception.InvalidValueException;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class ValueNotAllowedException
extends InvalidValueException {
    private final @Unmodifiable List<String> allowedValues;
    private final boolean caseSensitive;

    public ValueNotAllowedException(@NotNull String input, @NotNull @Unmodifiable List<String> allowedValues, boolean caseSensitive) {
        super(input);
        this.allowedValues = allowedValues;
        this.caseSensitive = caseSensitive;
    }

    public boolean caseSensitive() {
        return this.caseSensitive;
    }

    public @Unmodifiable @NotNull List<String> allowedValues() {
        return this.allowedValues;
    }
}

