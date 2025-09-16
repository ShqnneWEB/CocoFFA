/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.nbt;

import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.LongBinaryTagImpl;
import net.kyori.adventure.nbt.NumberBinaryTag;
import org.jetbrains.annotations.NotNull;

public interface LongBinaryTag
extends NumberBinaryTag {
    @NotNull
    public static LongBinaryTag of(long value) {
        return new LongBinaryTagImpl(value);
    }

    @NotNull
    default public BinaryTagType<LongBinaryTag> type() {
        return BinaryTagTypes.LONG;
    }

    public long value();
}

