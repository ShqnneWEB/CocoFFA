/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package hu.geri.libs.revxrsal.commands.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import org.jetbrains.annotations.NotNull;

public interface BrigadierConverter<A extends CommandActor, S> {
    @NotNull
    public ArgumentType<?> getArgumentType(@NotNull ParameterNode<A, ?> var1);

    @NotNull
    public A createActor(@NotNull S var1, @NotNull Lamp<A> var2);
}

