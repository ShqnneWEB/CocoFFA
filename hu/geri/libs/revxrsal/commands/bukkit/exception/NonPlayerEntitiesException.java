/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.bukkit.exception;

import hu.geri.libs.revxrsal.commands.exception.InvalidValueException;
import org.jetbrains.annotations.NotNull;

public class NonPlayerEntitiesException
extends InvalidValueException {
    public NonPlayerEntitiesException(@NotNull String input) {
        super(input);
    }
}

