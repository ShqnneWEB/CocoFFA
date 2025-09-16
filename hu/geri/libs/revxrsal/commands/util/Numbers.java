/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.util;

import hu.geri.libs.revxrsal.commands.util.Classes;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import org.jetbrains.annotations.NotNull;

public final class Numbers {
    private Numbers() {
        Preconditions.cannotInstantiate(Numbers.class);
    }

    @NotNull
    public static Number getMinValue(@NotNull Class<?> type) {
        if ((type = Classes.wrap(type)) == Byte.class) {
            return (byte)-128;
        }
        if (type == Short.class) {
            return (short)Short.MIN_VALUE;
        }
        if (type == Integer.class) {
            return Integer.MIN_VALUE;
        }
        if (type == Long.class) {
            return Long.MIN_VALUE;
        }
        if (type == Float.class) {
            return Float.valueOf(Float.MIN_VALUE);
        }
        if (type == Double.class) {
            return Double.MIN_VALUE;
        }
        throw new IllegalArgumentException("Unsupported type: " + type.getName());
    }

    @NotNull
    public static Number getMaxValue(@NotNull Class<?> type) {
        if ((type = Classes.wrap(type)) == Byte.class) {
            return (byte)127;
        }
        if (type == Short.class) {
            return (short)Short.MAX_VALUE;
        }
        if (type == Integer.class) {
            return Integer.MAX_VALUE;
        }
        if (type == Long.class) {
            return Long.MAX_VALUE;
        }
        if (type == Float.class) {
            return Float.valueOf(Float.MAX_VALUE);
        }
        if (type == Double.class) {
            return Double.MAX_VALUE;
        }
        throw new IllegalArgumentException("Unsupported type: " + type.getName());
    }

    public static boolean isDecimal(@NotNull Class<?> type) {
        return (type = Classes.wrap(type)) == Float.class || type == Double.class;
    }
}

