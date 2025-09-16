/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.command;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandParameter;
import hu.geri.libs.revxrsal.commands.reflect.MethodCaller;
import hu.geri.libs.revxrsal.commands.response.ResponseHandler;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public interface CommandFunction {
    public <A extends CommandActor> Lamp<A> lamp();

    @NotNull
    public String name();

    @NotNull
    public AnnotationList annotations();

    @NotNull
    public Method method();

    @NotNull
    public @Unmodifiable Map<String, CommandParameter> parametersByName();

    @NotNull
    public CommandParameter parameter(String var1);

    @NotNull
    public MethodCaller.BoundMethodCaller caller();

    public <T> T call(@NotNull Object ... var1);

    @NotNull
    public <T> ResponseHandler<?, T> responseHandler();

    default public boolean hasAnnotation(@NotNull Class<? extends Annotation> annotationType) {
        return this.annotations().contains(annotationType);
    }

    @Nullable
    default public <T extends Annotation> T getAnnotation(@NotNull Class<T> type) {
        return this.annotations().get(type);
    }
}

