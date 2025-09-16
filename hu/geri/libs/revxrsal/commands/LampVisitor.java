/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface LampVisitor<A extends CommandActor> {
    @NotNull
    public static <A extends CommandActor> LampVisitor<A> nothing() {
        return lamp -> {};
    }

    public void visit(@NotNull Lamp<A> var1);
}

