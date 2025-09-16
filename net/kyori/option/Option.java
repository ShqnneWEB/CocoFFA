/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.option;

import net.kyori.option.OptionSchema;
import net.kyori.option.value.ValueType;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
public interface Option<V> {
    @Deprecated
    public static Option<Boolean> booleanOption(String id, boolean defaultValue) {
        return OptionSchema.globalSchema().booleanOption(id, defaultValue);
    }

    @Deprecated
    public static <E extends Enum<E>> Option<E> enumOption(String id, Class<E> enumClazz, E defaultValue) {
        return OptionSchema.globalSchema().enumOption(id, enumClazz, defaultValue);
    }

    public String id();

    @Deprecated
    default public Class<V> type() {
        return this.valueType().type();
    }

    public ValueType<V> valueType();

    public @Nullable V defaultValue();
}

