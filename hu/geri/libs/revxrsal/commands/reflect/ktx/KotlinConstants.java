/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.reflect.ktx;

import hu.geri.libs.revxrsal.commands.util.Lazy;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class KotlinConstants {
    public static final Object ABSENT_VALUE = new Object();
    private static final Supplier<Class<?>> CONTINUATION = Lazy.of(() -> KotlinConstants.findClass("kotlin.coroutines.Continuation"));
    private static final Supplier<Class<? extends Annotation>> METADATA = Lazy.of(() -> {
        Class<?> metadata = KotlinConstants.findClass("kotlin.Metadata");
        return metadata == null ? null : metadata.asSubclass(Annotation.class);
    });

    private KotlinConstants() {
    }

    @Nullable
    private static Class<?> findClass(@NotNull String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static boolean isStaticFinal(int modifiers) {
        return Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
    }

    @Nullable
    public static Object defaultPrimitiveValue(Class<?> type) {
        if (type == Integer.TYPE) {
            return 0;
        }
        if (type == Long.TYPE) {
            return 0L;
        }
        if (type == Float.TYPE) {
            return Float.valueOf(0.0f);
        }
        if (type == Double.TYPE) {
            return 0.0;
        }
        if (type == Short.TYPE) {
            return (short)0;
        }
        if (type == Byte.TYPE) {
            return (byte)0;
        }
        if (type == Boolean.TYPE) {
            return false;
        }
        if (type == Character.TYPE) {
            return Character.valueOf('\u0000');
        }
        if (type == Optional.class) {
            return Optional.empty();
        }
        return null;
    }

    @Nullable
    public static Class<?> continuation() {
        return CONTINUATION.get();
    }

    public static boolean isKotlinClass(@NotNull Class<?> cl) {
        return METADATA.get() != null && cl.isAnnotationPresent(METADATA.get());
    }
}

