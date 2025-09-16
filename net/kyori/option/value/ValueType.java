/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.option.value;

import java.util.Objects;
import net.kyori.option.value.ValueTypeImpl;

public interface ValueType<T> {
    public static ValueType<String> stringType() {
        return ValueTypeImpl.Types.STRING;
    }

    public static ValueType<Boolean> booleanType() {
        return ValueTypeImpl.Types.BOOLEAN;
    }

    public static ValueType<Integer> integerType() {
        return ValueTypeImpl.Types.INT;
    }

    public static ValueType<Double> doubleType() {
        return ValueTypeImpl.Types.DOUBLE;
    }

    public static <E extends Enum<E>> ValueType<E> enumType(Class<E> enumClazz) {
        return new ValueTypeImpl.EnumType<E>(Objects.requireNonNull(enumClazz, "enumClazz"));
    }

    public Class<T> type();

    public T parse(String var1) throws IllegalArgumentException;
}

