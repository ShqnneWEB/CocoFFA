/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.exception.ThrowableFromCommand;
import org.jetbrains.annotations.NotNull;

@ThrowableFromCommand
public class UnknownParameterException
extends RuntimeException {
    @NotNull
    private final String name;
    private final boolean shorthand;

    public UnknownParameterException(@NotNull String name, boolean shorthand) {
        this.name = name;
        this.shorthand = shorthand;
    }

    @NotNull
    public String name() {
        return this.name;
    }

    public boolean shorthand() {
        return this.shorthand;
    }
}

