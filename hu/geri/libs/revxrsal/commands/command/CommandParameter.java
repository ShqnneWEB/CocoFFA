/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.command;

import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CommandParameter {
    @NotNull
    public String name();

    public int methodIndex();

    public boolean isLastInMethod();

    @NotNull
    public Parameter parameter();

    @NotNull
    public Method method();

    @NotNull
    public AnnotationList annotations();

    @NotNull
    public Class<?> type();

    @NotNull
    public Type fullType();

    @NotNull
    public List<Type> generics();

    public boolean isOptional();

    default public boolean hasAnnotation(@NotNull Class<? extends Annotation> annotationType) {
        return this.annotations().contains(annotationType);
    }

    @Nullable
    default public <T extends Annotation> T getAnnotation(@NotNull Class<T> type) {
        return this.annotations().get(type);
    }
}

