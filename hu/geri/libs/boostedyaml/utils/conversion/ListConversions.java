/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.utils.conversion;

import hu.geri.libs.boostedyaml.utils.conversion.PrimitiveConversions;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ListConversions {
    @NotNull
    public static Optional<List<String>> toStringList(@Nullable List<?> value) {
        return ListConversions.construct(value, o -> Optional.ofNullable(o.toString()));
    }

    @NotNull
    public static Optional<List<Integer>> toIntList(@Nullable List<?> value) {
        return ListConversions.construct(value, PrimitiveConversions::toInt);
    }

    @NotNull
    public static Optional<List<BigInteger>> toBigIntList(@Nullable List<?> value) {
        return ListConversions.construct(value, PrimitiveConversions::toBigInt);
    }

    @NotNull
    public static Optional<List<Byte>> toByteList(@Nullable List<?> value) {
        return ListConversions.construct(value, PrimitiveConversions::toByte);
    }

    @NotNull
    public static Optional<List<Long>> toLongList(@Nullable List<?> value) {
        return ListConversions.construct(value, PrimitiveConversions::toLong);
    }

    @NotNull
    public static Optional<List<Double>> toDoubleList(@Nullable List<?> value) {
        return ListConversions.construct(value, PrimitiveConversions::toDouble);
    }

    @NotNull
    public static Optional<List<Float>> toFloatList(@Nullable List<?> value) {
        return ListConversions.construct(value, PrimitiveConversions::toFloat);
    }

    @NotNull
    public static Optional<List<Short>> toShortList(@Nullable List<?> value) {
        return ListConversions.construct(value, PrimitiveConversions::toShort);
    }

    @NotNull
    public static Optional<List<Map<?, ?>>> toMapList(@Nullable List<?> value) {
        return ListConversions.construct(value, o -> o instanceof Map ? Optional.of((Map)o) : Optional.empty());
    }

    @NotNull
    private static <T> Optional<List<T>> construct(@Nullable List<?> value, @NotNull Function<Object, Optional<T>> mapper) {
        if (value == null) {
            return Optional.empty();
        }
        ArrayList list = new ArrayList();
        for (Object element : value) {
            if (element == null) continue;
            mapper.apply(element).ifPresent(list::add);
        }
        return Optional.of(list);
    }
}

