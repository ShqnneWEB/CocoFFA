/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.util;

import java.util.Objects;
import org.jetbrains.annotations.Contract;

public final class Preconditions {
    private Preconditions() {
        Preconditions.cannotInstantiate(Preconditions.class);
    }

    public static <T> void notEmpty(T[] array, String errorMessage) {
        if (array.length == 0) {
            throw new IllegalStateException(errorMessage);
        }
    }

    @Contract(value="null, _ -> fail; !null, _ -> param1")
    public static <T> T notNull(T t, String err) {
        return Objects.requireNonNull(t, err + " cannot be null!");
    }

    @Contract(value="_ -> fail")
    public static void cannotInstantiate(Class<?> clazz) {
        throw new UnsupportedOperationException("Cannot instantiate " + clazz + "!");
    }
}

