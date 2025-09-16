/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.util;

import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Classes {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER;
    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE;

    private Classes() {
        Preconditions.cannotInstantiate(Classes.class);
    }

    public static Class<?> getType(@NotNull Object o) {
        return o instanceof Class ? (Class<?>)o : o.getClass();
    }

    public static <T> Class<T> wrap(Class<T> type) {
        Preconditions.notNull(type, "type");
        Class<?> wrapped = PRIMITIVE_TO_WRAPPER.get(type);
        return wrapped == null ? type : wrapped;
    }

    @Nullable
    public static Type arrayComponentType(Type type) {
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType)type).getGenericComponentType();
        }
        if (type instanceof Class) {
            return ((Class)type).getComponentType();
        }
        return null;
    }

    public static <T> Class<T> unwrap(Class<T> type) {
        Preconditions.notNull(type, "type");
        Class<?> unwrapped = WRAPPER_TO_PRIMITIVE.get(type);
        return unwrapped == null ? type : unwrapped;
    }

    public static boolean isWrapperType(Class<?> type) {
        Preconditions.notNull(type, "type");
        return WRAPPER_TO_PRIMITIVE.containsKey(type);
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Type rawType = parameterizedType.getRawType();
            if (!(rawType instanceof Class)) {
                throw new IllegalStateException("Expected a Class, found a " + rawType);
            }
            return (Class)rawType;
        }
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType)type).getGenericComponentType();
            return Array.newInstance(Classes.getRawType(componentType), 0).getClass();
        }
        if (type instanceof TypeVariable) {
            return Object.class;
        }
        if (type instanceof WildcardType) {
            return Classes.getRawType(((WildcardType)type).getUpperBounds()[0]);
        }
        String className = type == null ? "null" : type.getClass().getName();
        throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + className);
    }

    @Deprecated
    @Contract(value="_,_ -> param2")
    public static Type getFirstGeneric(@NotNull Class<?> cl, @NotNull Type fallback) {
        return fallback;
    }

    public static Type getFirstGeneric(@NotNull Type genericType, @NotNull Type fallback) {
        try {
            return ((ParameterizedType)genericType).getActualTypeArguments()[0];
        } catch (ClassCastException e) {
            return fallback;
        }
    }

    public static boolean isClassPresent(@NotNull String name) {
        try {
            Class.forName(name);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static void addPrimitive(Map<Class<?>, Class<?>> forward, Map<Class<?>, Class<?>> backward, Class<?> key, Class<?> value) {
        forward.put(key, value);
        backward.put(value, key);
    }

    public static void checkRetention(@NotNull Class<? extends Annotation> type) {
        if (!type.isAnnotationPresent(Retention.class) || type.getAnnotation(Retention.class).value() != RetentionPolicy.RUNTIME) {
            throw new IllegalArgumentException("Tried to check for annotation @" + type.getName() + ", but it does not have @Retention(RetentionPolicy.RUNTIME)! As such, it may be present but we cannot see it.");
        }
    }

    static {
        LinkedHashMap primToWrap = new LinkedHashMap(16);
        LinkedHashMap wrapToPrim = new LinkedHashMap(16);
        Classes.addPrimitive(primToWrap, wrapToPrim, Boolean.TYPE, Boolean.class);
        Classes.addPrimitive(primToWrap, wrapToPrim, Byte.TYPE, Byte.class);
        Classes.addPrimitive(primToWrap, wrapToPrim, Character.TYPE, Character.class);
        Classes.addPrimitive(primToWrap, wrapToPrim, Double.TYPE, Double.class);
        Classes.addPrimitive(primToWrap, wrapToPrim, Float.TYPE, Float.class);
        Classes.addPrimitive(primToWrap, wrapToPrim, Integer.TYPE, Integer.class);
        Classes.addPrimitive(primToWrap, wrapToPrim, Long.TYPE, Long.class);
        Classes.addPrimitive(primToWrap, wrapToPrim, Short.TYPE, Short.class);
        Classes.addPrimitive(primToWrap, wrapToPrim, Void.TYPE, Void.class);
        PRIMITIVE_TO_WRAPPER = Collections.unmodifiableMap(primToWrap);
        WRAPPER_TO_PRIMITIVE = Collections.unmodifiableMap(wrapToPrim);
    }
}

