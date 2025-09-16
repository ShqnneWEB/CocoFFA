/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.exception.ThrowableFromCommand;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

@ThrowableFromCommand
public class CooldownException
extends RuntimeException {
    private final long timeLeft;

    public CooldownException(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    public CooldownException(TimeUnit unit, long timeLeft) {
        this.timeLeft = unit.toMillis(timeLeft);
    }

    public long getTimeLeftMillis() {
        return this.timeLeft;
    }

    public long getTimeLeft(@NotNull TimeUnit unit) {
        Preconditions.notNull(unit, "unit");
        return unit.convert(this.timeLeft, TimeUnit.MILLISECONDS);
    }
}

