/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.option;

import java.util.Objects;
import net.kyori.option.Option;
import net.kyori.option.value.ValueType;
import org.jspecify.annotations.Nullable;

final class OptionImpl<V>
implements Option<V> {
    private final String id;
    private final ValueType<V> type;
    private final @Nullable V defaultValue;

    OptionImpl(String id, ValueType<V> type, @Nullable V defaultValue) {
        this.id = id;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public ValueType<V> valueType() {
        return this.type;
    }

    @Override
    public @Nullable V defaultValue() {
        return this.defaultValue;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        OptionImpl that = (OptionImpl)other;
        return Objects.equals(this.id, that.id) && Objects.equals(this.type, that.type);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.type);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{id=" + this.id + ",type=" + this.type + ",defaultValue=" + this.defaultValue + '}';
    }
}

