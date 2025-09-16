/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.exception.InvalidValueException;
import org.jetbrains.annotations.NotNull;

public class EnumNotFoundException
extends InvalidValueException {
    private final Class<? extends Enum> enumType;

    public EnumNotFoundException(@NotNull String input, Class<? extends Enum> enumType) {
        super(input);
        this.enumType = enumType;
    }

    public Class<? extends Enum> enumType() {
        return this.enumType;
    }
}

