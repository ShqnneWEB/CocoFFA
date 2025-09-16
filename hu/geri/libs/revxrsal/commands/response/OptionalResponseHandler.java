/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.response;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.response.ResponseHandler;
import hu.geri.libs.revxrsal.commands.util.Classes;
import java.lang.reflect.Type;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum OptionalResponseHandler implements ResponseHandler.Factory<CommandActor>
{
    INSTANCE;


    @Override
    @Nullable
    public <T> ResponseHandler<CommandActor, T> create(@NotNull Type type, @NotNull AnnotationList annotations, @NotNull Lamp<CommandActor> lamp) {
        Class<?> rawType = Classes.getRawType(type);
        if (rawType != Optional.class) {
            return null;
        }
        Type suppliedType = Classes.getFirstGeneric(type, Object.class);
        ResponseHandler delegate = lamp.responseHandler(suppliedType, AnnotationList.empty());
        return (response, context) -> {
            Optional optional = (Optional)response;
            optional.ifPresent(value -> delegate.handleResponse(value, context));
        };
    }
}

