/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.util;

import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class InstanceCreator {
    private InstanceCreator() {
        Preconditions.cannotInstantiate(InstanceCreator.class);
    }

    @NotNull
    public static <T> T create(@NotNull Class<? extends T> type) {
        T t;
        if (type.isAnnotation()) {
            throw new IllegalArgumentException("Cannot construct annotation types");
        }
        if (type.isArray()) {
            return (T)Array.newInstance(type, 0);
        }
        if (type.isEnum()) {
            return (T)InstanceCreator.firstEnum(type.asSubclass(Enum.class));
        }
        if (type.isInterface()) {
            T singleton = InstanceCreator.fromSingletonField(type);
            if (singleton != null) {
                return singleton;
            }
            singleton = InstanceCreator.fromGetter(type);
            if (singleton == null) {
                throw new IllegalArgumentException("Attempted to construct an interface that has no getInstance()-like methods or INSTANCE-like fields");
            }
        }
        if ((t = InstanceCreator.fromNoArgConstructor(type)) == null) {
            t = InstanceCreator.fromSingletonField(type);
        }
        if (t != null) {
            return t;
        }
        t = InstanceCreator.fromGetter(type);
        if (t == null) {
            throw new IllegalArgumentException("Attempted to construct a class that has no getInstance() or INSTANCE, singletons, or a no-arg constructor.");
        }
        return t;
    }

    @Nullable
    private static <T> T fromNoArgConstructor(Class<? extends T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);
            return constructor.newInstance(new Object[0]);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    private static <T> T fromGetter(Class<? extends T> type) {
        for (Method method : type.getDeclaredMethods()) {
            if (!type.isAssignableFrom(method.getReturnType()) || !Modifier.isStatic(method.getModifiers()) || method.getParameterCount() != 0) continue;
            method.setAccessible(true);
            try {
                return (T)method.invoke(null, new Object[0]);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @NotNull
    private static <T extends Enum> T firstEnum(Class<? extends T> type) {
        Enum[] values = (Enum[])type.getEnumConstants();
        if (values.length == 0) {
            throw new IllegalArgumentException("Attempted to construct an enum that has no fields");
        }
        return (T)values[0];
    }

    @Nullable
    private static <T> T fromSingletonField(Class<? extends T> type) {
        for (Field field : type.getDeclaredFields()) {
            if (!type.isAssignableFrom(field.getType()) || !Modifier.isStatic(field.getModifiers())) continue;
            field.setAccessible(true);
            try {
                return (T)field.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}

