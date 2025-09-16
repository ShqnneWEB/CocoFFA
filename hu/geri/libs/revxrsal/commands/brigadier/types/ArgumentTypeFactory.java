/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package hu.geri.libs.revxrsal.commands.brigadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.util.Classes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ArgumentTypeFactory<A extends CommandActor> {
    @NotNull
    public static <A extends CommandActor> ArgumentTypeFactory<A> forType(Class<?> type, ArgumentType<?> argumentType) {
        Class<?> wrapped = Classes.wrap(type);
        return parameter -> Classes.wrap(parameter.type()) == wrapped ? argumentType : null;
    }

    @NotNull
    public static <A extends CommandActor> ArgumentTypeFactory<A> forTypeAndSubclasses(Class<?> type, ArgumentType<?> argumentType) {
        Class<?> wrapped = Classes.wrap(type);
        return parameter -> wrapped.isAssignableFrom(Classes.wrap(parameter.type())) ? argumentType : null;
    }

    @Nullable
    public ArgumentType<?> getArgumentType(@NotNull ParameterNode<A, ?> var1);
}

