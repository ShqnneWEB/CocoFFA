/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.nbt;

import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagType;
import org.jetbrains.annotations.NotNull;

public interface NumberBinaryTag
extends BinaryTag {
    @NotNull
    public BinaryTagType<? extends NumberBinaryTag> type();

    public byte byteValue();

    public double doubleValue();

    public float floatValue();

    public int intValue();

    public long longValue();

    public short shortValue();
}

