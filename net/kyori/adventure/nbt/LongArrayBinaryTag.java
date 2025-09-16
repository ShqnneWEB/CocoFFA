/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.nbt;

import java.util.function.LongConsumer;
import java.util.stream.LongStream;
import net.kyori.adventure.nbt.ArrayBinaryTag;
import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.LongArrayBinaryTagImpl;
import org.jetbrains.annotations.NotNull;

public interface LongArrayBinaryTag
extends ArrayBinaryTag,
Iterable<Long> {
    @NotNull
    public static LongArrayBinaryTag of(long @NotNull ... value) {
        return new LongArrayBinaryTagImpl(value);
    }

    @NotNull
    default public BinaryTagType<LongArrayBinaryTag> type() {
        return BinaryTagTypes.LONG_ARRAY;
    }

    public long @NotNull [] value();

    public int size();

    public long get(int var1);

    public  @NotNull PrimitiveIterator.OfLong iterator();

    public  @NotNull Spliterator.OfLong spliterator();

    @NotNull
    public LongStream stream();

    public void forEachLong(@NotNull LongConsumer var1);
}

