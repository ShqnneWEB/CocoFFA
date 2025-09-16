/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.nbt;

import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.IntBinaryTagImpl;
import net.kyori.adventure.nbt.NumberBinaryTag;
import org.jetbrains.annotations.NotNull;

public interface IntBinaryTag
extends NumberBinaryTag {
    @NotNull
    public static IntBinaryTag of(int value) {
        return new IntBinaryTagImpl(value);
    }

    @NotNull
    default public BinaryTagType<IntBinaryTag> type() {
        return BinaryTagTypes.INT;
    }

    public int value();
}

