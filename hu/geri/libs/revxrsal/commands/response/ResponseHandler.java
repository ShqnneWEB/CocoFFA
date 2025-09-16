/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.response;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.response.ClassResponseHandlerFactory;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ResponseHandler<A extends CommandActor, T> {
    @NotNull
    public static <A extends CommandActor, T> ResponseHandler<A, T> noOp() {
        return (response, context) -> {};
    }

    public void handleResponse(T var1, ExecutionContext<A> var2);

    public static interface Factory<A extends CommandActor> {
        @NotNull
        public static <A extends CommandActor, T> Factory<A> forType(@NotNull Class<T> type, @NotNull ResponseHandler<A, T> responseHandler) {
            return new ClassResponseHandlerFactory<A, T>(type, responseHandler, false);
        }

        @NotNull
        public static <A extends CommandActor, T> Factory<A> forTypeAndSubclasses(@NotNull Class<T> type, @NotNull ResponseHandler<A, T> responseHandler) {
            return new ClassResponseHandlerFactory<A, T>(type, responseHandler, true);
        }

        @Nullable
        public <T> ResponseHandler<A, T> create(@NotNull Type var1, @NotNull AnnotationList var2, @NotNull Lamp<A> var3);
    }
}

