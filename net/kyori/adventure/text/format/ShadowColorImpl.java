/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.format;

import java.util.stream.Stream;
import net.kyori.adventure.internal.Internals;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ShadowColorImpl
implements ShadowColor,
Examinable {
    static final int NONE_VALUE = 0;
    static final ShadowColorImpl NONE = new ShadowColorImpl(0);
    private final int value;

    ShadowColorImpl(int value) {
        this.value = value;
    }

    @Override
    public int value() {
        return this.value;
    }

    public boolean equals(@Nullable Object other) {
        if (!(other instanceof ShadowColorImpl)) {
            return false;
        }
        ShadowColorImpl that = (ShadowColorImpl)other;
        return this.value == that.value;
    }

    public int hashCode() {
        return Integer.hashCode(this.value);
    }

    public String toString() {
        return Internals.toString(this);
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("value", this.value));
    }
}

