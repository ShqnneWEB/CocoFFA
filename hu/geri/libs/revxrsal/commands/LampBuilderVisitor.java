/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface LampBuilderVisitor<A extends CommandActor> {
    @NotNull
    public static <A extends CommandActor> LampBuilderVisitor<A> nothing() {
        return builder -> {};
    }

    public void visit(@NotNull Lamp.Builder<A> var1);
}

