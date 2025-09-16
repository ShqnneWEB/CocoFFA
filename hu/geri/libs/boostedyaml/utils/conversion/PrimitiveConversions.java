/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.utils.conversion;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrimitiveConversions {
    public static final Map<Class<?>, Class<?>> NUMERIC_PRIMITIVES = Collections.unmodifiableMap(new HashMap<Class<?>, Class<?>>(){
        {
            this.put(Integer.TYPE, Integer.class);
            this.put(Byte.TYPE, Byte.class);
            this.put(Short.TYPE, Short.class);
            this.put(Long.TYPE, Long.class);
            this.put(Float.TYPE, Float.class);
            this.put(Double.TYPE, Double.class);
        }
    });
    public static final Map<Class<?>, Class<?>> PRIMITIVES_TO_OBJECTS = Collections.unmodifiableMap(new HashMap<Class<?>, Class<?>>(){
        {
            this.putAll(NUMERIC_PRIMITIVES);
            this.put(Boolean.TYPE, Boolean.class);
            this.put(Character.TYPE, Character.class);
        }
    });
    public static final Map<Class<?>, Class<?>> NON_NUMERIC_CONVERSIONS = Collections.unmodifiableMap(new HashMap<Class<?>, Class<?>>(){
        {
            this.put(Boolean.TYPE, Boolean.class);
            this.put(Character.TYPE, Character.class);
            this.put(Boolean.class, Boolean.TYPE);
            this.put(Character.class, Character.TYPE);
        }
    });
    public static final Set<Class<?>> NUMERIC_CLASSES = Collections.unmodifiableSet(new HashSet<Class<?>>(){
        {
            this.add(Integer.TYPE);
            this.add(Byte.TYPE);
            this.add(Short.TYPE);
            this.add(Long.TYPE);
            this.add(Float.TYPE);
            this.add(Double.TYPE);
            this.add(Integer.class);
            this.add(Byte.class);
            this.add(Short.class);
            this.add(Long.class);
            this.add(Float.class);
            this.add(Double.class);
        }
    });

    public static boolean isNumber(@NotNull Class<?> clazz) {
        return NUMERIC_CLASSES.contains(clazz);
    }

    public static Object convertNumber(@NotNull Object value, @NotNull Class<?> target) {
        Number number = (Number)value;
        boolean primitive = target.isPrimitive();
        if (primitive) {
            target = NUMERIC_PRIMITIVES.get(target);
        }
        if (target == Integer.class) {
            return number.intValue();
        }
        if (target == Byte.class) {
            return number.byteValue();
        }
        if (target == Short.class) {
            return number.shortValue();
        }
        if (target == Long.class) {
            return number.longValue();
        }
        if (target == Float.class) {
            return Float.valueOf(number.floatValue());
        }
        return number.doubleValue();
    }

    public static Optional<Integer> toInt(@Nullable Object value) {
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof Number) {
            return Optional.of(((Number)value).intValue());
        }
        try {
            return Optional.of(Integer.valueOf(value.toString()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public static Optional<Byte> toByte(@Nullable Object value) {
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof Number) {
            return Optional.of(((Number)value).byteValue());
        }
        try {
            return Optional.of(Byte.valueOf(value.toString()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public static Optional<Long> toLong(@Nullable Object value) {
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof Number) {
            return Optional.of(((Number)value).longValue());
        }
        try {
            return Optional.of(Long.valueOf(value.toString()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public static Optional<Double> toDouble(@Nullable Object value) {
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof Number) {
            return Optional.of(((Number)value).doubleValue());
        }
        try {
            return Optional.of(Double.valueOf(value.toString()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public static Optional<Float> toFloat(@Nullable Object value) {
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof Number) {
            return Optional.of(Float.valueOf(((Number)value).floatValue()));
        }
        try {
            return Optional.of(Float.valueOf(value.toString()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public static Optional<Short> toShort(@Nullable Object value) {
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof Number) {
            return Optional.of(((Number)value).shortValue());
        }
        try {
            return Optional.of(Short.valueOf(value.toString()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public static Optional<BigInteger> toBigInt(@Nullable Object value) {
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof BigInteger) {
            return Optional.of((BigInteger)value);
        }
        if (value instanceof Number) {
            return Optional.of(BigInteger.valueOf(((Number)value).longValue()));
        }
        try {
            return Optional.of(new BigInteger(value.toString()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }
}

