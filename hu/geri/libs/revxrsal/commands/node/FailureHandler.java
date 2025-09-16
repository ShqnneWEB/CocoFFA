/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.Potential;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@FunctionalInterface
public interface FailureHandler<A extends CommandActor> {
    public void handleFailedAttempts(@NotNull A var1, @NotNull @Unmodifiable List<Potential<A>> var2, @NotNull StringStream var3);
}

