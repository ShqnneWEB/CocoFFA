/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.nbt;

import java.util.stream.Stream;
import net.kyori.adventure.nbt.AbstractBinaryTag;
import net.kyori.adventure.nbt.FloatBinaryTag;
import net.kyori.adventure.nbt.ShadyPines;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.Debug;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Debug.Renderer(text="String.valueOf(this.value) + \"f\"", hasChildren="false")
final class FloatBinaryTagImpl
extends AbstractBinaryTag
implements FloatBinaryTag {
    private final float value;

    FloatBinaryTagImpl(float value) {
        this.value = value;
    }

    @Override
    public float value() {
        return this.value;
    }

    @Override
    public byte byteValue() {
        return (byte)(ShadyPines.floor(this.value) & 0xFF);
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
        return this.value;
    }

    @Override
    public int intValue() {
        return ShadyPines.floor(this.value);
    }

    @Override
    public long longValue() {
        return (long)this.value;
    }

    @Override
    public short shortValue() {
        return (short)(ShadyPines.floor(this.value) & 0xFFFF);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        FloatBinaryTagImpl that = (FloatBinaryTagImpl)other;
        return Float.floatToIntBits(this.value) == Float.floatToIntBits(that.value);
    }

    public int hashCode() {
        return Float.hashCode(this.value);
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("value", this.value));
    }
}

