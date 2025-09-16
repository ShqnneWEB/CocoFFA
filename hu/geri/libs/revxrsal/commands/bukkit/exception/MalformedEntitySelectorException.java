/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.bukkit.exception;

import hu.geri.libs.revxrsal.commands.exception.InvalidValueException;
import org.jetbrains.annotations.NotNull;

public class MalformedEntitySelectorException
extends InvalidValueException {
    @NotNull
    private final String errorMessage;

    public MalformedEntitySelectorException(String input, @NotNull String errorMessage) {
        super(input);
        this.errorMessage = errorMessage;
    }

    @NotNull
    public String errorMessage() {
        return this.errorMessage;
    }
}

