/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.process;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ParameterValidator<A extends CommandActor, T> {
    public void validate(@NotNull A var1, T var2, @NotNull ParameterNode<A, T> var3, @NotNull Lamp<A> var4);
}

