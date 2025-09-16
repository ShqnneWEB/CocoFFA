/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.CheckReturnValue
 */
package hu.geri.libs.revxrsal.commands.command;

import hu.geri.libs.revxrsal.commands.exception.CooldownException;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@ApiStatus.Experimental
public interface CooldownHandle {
    @CheckReturnValue
    @Contract(pure=true, value="_, _ -> new")
    @NotNull
    public CooldownHandle withCooldown(long var1, @NotNull TimeUnit var3);

    public boolean isOnCooldown();

    public long elapsedMillis();

    public void cooldown();

    public void requireNotOnCooldown() throws CooldownException;

    public void requireNotOnCooldown(long var1, @NotNull TimeUnit var3) throws CooldownException;

    public void removeCooldown();

    default public long elapsed(@NotNull TimeUnit outputUnit) {
        return outputUnit.convert(this.elapsedMillis(), TimeUnit.MILLISECONDS);
    }

    public void cooldown(@Range(from=1L, to=0x7FFFFFFFFFFFFFFFL) long var1, @NotNull TimeUnit var3);

    public long remainingTimeMillis();

    public long remainingTime(@NotNull TimeUnit var1);

    default public long remainingTimeMillis(long cooldownValue, @NotNull TimeUnit cooldownUnit) {
        long cooldownMillis = cooldownUnit.toMillis(cooldownValue);
        return cooldownMillis - this.elapsedMillis();
    }

    default public long remainingTime(long cooldownValue, @NotNull TimeUnit cooldownUnit) {
        return this.remainingTime(cooldownValue, cooldownUnit, cooldownUnit);
    }

    default public long remainingTime(long cooldownValue, @NotNull TimeUnit cooldownUnit, @NotNull TimeUnit outputUnit) {
        long cooldownMillis = cooldownUnit.toMillis(cooldownValue);
        return outputUnit.convert(cooldownMillis - this.elapsedMillis(), TimeUnit.MILLISECONDS);
    }
}

