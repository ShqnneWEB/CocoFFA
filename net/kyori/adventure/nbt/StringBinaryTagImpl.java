/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.nbt;

import java.util.stream.Stream;
import net.kyori.adventure.nbt.AbstractBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.Debug;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Debug.Renderer(text="\"\\\"\" + this.value + \"\\\"\"", hasChildren="false")
final class StringBinaryTagImpl
extends AbstractBinaryTag
implements StringBinaryTag {
    private final String value;

    StringBinaryTagImpl(String value) {
        this.value = value;
    }

    @Override
    @NotNull
    public String value() {
        return this.value;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        StringBinaryTagImpl that = (StringBinaryTagImpl)other;
        return this.value.equals(that.value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("value", this.value));
    }
}

