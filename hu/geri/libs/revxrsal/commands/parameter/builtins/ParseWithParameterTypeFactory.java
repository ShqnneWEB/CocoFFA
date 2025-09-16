/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter.builtins;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.ParseWith;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.parameter.BaseParameterType;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.util.InstanceCreator;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ParseWithParameterTypeFactory implements ParameterType.Factory<CommandActor>
{
    INSTANCE;


    @Override
    @Nullable
    public <T> ParameterType<CommandActor, T> create(@NotNull Type parameterType, @NotNull AnnotationList annotations, @NotNull Lamp<CommandActor> lamp) {
        ParseWith parseWith = annotations.get(ParseWith.class);
        if (parseWith == null) {
            return null;
        }
        BaseParameterType type = InstanceCreator.create(parseWith.value());
        if (type instanceof ParameterType) {
            ParameterType pType = (ParameterType)type;
            return pType;
        }
        if (type instanceof ParameterType.Factory) {
            ParameterType.Factory factory = (ParameterType.Factory)type;
            return factory.create(parameterType, annotations, lamp);
        }
        throw new IllegalArgumentException("Don't know how to create a ParameterType from " + type);
    }
}

