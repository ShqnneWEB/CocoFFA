/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter.builtins;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.util.Classes;
import java.lang.reflect.Type;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum OptionalParameterTypeFactory implements ParameterType.Factory<CommandActor>
{
    INSTANCE;


    @Override
    @Nullable
    public <T> ParameterType<CommandActor, T> create(@NotNull Type parameterType, @NotNull AnnotationList annotations, @NotNull Lamp<CommandActor> lamp) {
        Class<?> rawType = Classes.getRawType(parameterType);
        if (rawType != Optional.class) {
            return null;
        }
        Type delegateType = Classes.getFirstGeneric(parameterType, Object.class);
        ParameterType delegate = lamp.resolver(delegateType, annotations).requireParameterType();
        return (input, context) -> Optional.of(delegate.parse(input, context));
    }
}

