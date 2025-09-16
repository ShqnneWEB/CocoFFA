/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.response;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.context.ErrorContext;
import hu.geri.libs.revxrsal.commands.response.ResponseHandler;
import hu.geri.libs.revxrsal.commands.util.Classes;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum CompletionStageResponseHandler implements ResponseHandler.Factory<CommandActor>
{
    INSTANCE;


    @Override
    @Nullable
    public <T> ResponseHandler<CommandActor, T> create(@NotNull Type type, @NotNull AnnotationList annotations, @NotNull Lamp<CommandActor> lamp) {
        Class<?> rawType = Classes.getRawType(type);
        if (rawType != CompletionStage.class && rawType != CompletableFuture.class) {
            return null;
        }
        Type suppliedType = Classes.getFirstGeneric(type, Object.class);
        ResponseHandler delegate = lamp.responseHandler(suppliedType, AnnotationList.empty());
        return (response, context) -> {
            CompletionStage future = (CompletionStage)response;
            future.whenComplete((o, throwable) -> {
                if (throwable != null) {
                    lamp.handleException((Throwable)throwable, ErrorContext.executingFunction(context));
                } else {
                    delegate.handleResponse(o, context);
                }
            });
        };
    }
}

