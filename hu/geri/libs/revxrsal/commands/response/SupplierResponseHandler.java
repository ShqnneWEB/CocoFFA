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
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SupplierResponseHandler implements ResponseHandler.Factory<CommandActor>
{
    INSTANCE;


    @Override
    @Nullable
    public <T> ResponseHandler<CommandActor, T> create(@NotNull Type type, @NotNull AnnotationList annotations, @NotNull Lamp<CommandActor> lamp) {
        Class<?> rawType = Classes.getRawType(type);
        if (rawType != Supplier.class) {
            return null;
        }
        Type optionalType = Classes.getFirstGeneric(type, Object.class);
        ResponseHandler delegate = lamp.responseHandler(optionalType, AnnotationList.empty());
        return (response, context) -> {
            Supplier supplier = (Supplier)response;
            Object v = supplier.get();
            delegate.handleResponse(v, context);
        };
    }
}

