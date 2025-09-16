/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package hu.geri.libs.revxrsal.commands.brigadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import hu.geri.libs.revxrsal.commands.brigadier.annotations.BType;
import hu.geri.libs.revxrsal.commands.brigadier.types.ArgumentTypeFactory;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.util.InstanceCreator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum BTypeFactory implements ArgumentTypeFactory<CommandActor>
{
    INSTANCE;


    @Override
    @Nullable
    public ArgumentType<?> getArgumentType(@NotNull ParameterNode<CommandActor, ?> parameter) {
        BType b = parameter.annotations().get(BType.class);
        if (b == null) {
            return null;
        }
        Object v = InstanceCreator.create(b.value());
        if (v instanceof ArgumentTypeFactory) {
            ArgumentTypeFactory factory = (ArgumentTypeFactory)v;
            return factory.getArgumentType(parameter);
        }
        if (v instanceof ArgumentType) {
            return (ArgumentType)v;
        }
        throw new IllegalArgumentException("Don't know how to create an ArgumentType from @BType(" + b.value().getName() + ".class)");
    }
}

