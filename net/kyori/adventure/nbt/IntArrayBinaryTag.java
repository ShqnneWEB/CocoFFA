/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.nbt;

import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import net.kyori.adventure.nbt.ArrayBinaryTag;
import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.IntArrayBinaryTagImpl;
import org.jetbrains.annotations.NotNull;

public interface IntArrayBinaryTag
extends ArrayBinaryTag,
Iterable<Integer> {
    @NotNull
    public static IntArrayBinaryTag of(int @NotNull ... value) {
        return new IntArrayBinaryTagImpl(value);
    }

    @NotNull
    default public BinaryTagType<IntArrayBinaryTag> type() {
        return BinaryTagTypes.INT_ARRAY;
    }

    public int @NotNull [] value();

    public int size();

    public int get(int var1);

    public  @NotNull PrimitiveIterator.OfInt iterator();

    public  @NotNull Spliterator.OfInt spliterator();

    @NotNull
    public IntStream stream();

    public void forEachInt(@NotNull IntConsumer var1);
}

