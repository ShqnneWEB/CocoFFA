/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.nbt;

import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.StringBinaryTagImpl;
import org.jetbrains.annotations.NotNull;

public interface StringBinaryTag
extends BinaryTag {
    @NotNull
    public static StringBinaryTag of(@NotNull String value) {
        return new StringBinaryTagImpl(value);
    }

    @NotNull
    default public BinaryTagType<StringBinaryTag> type() {
        return BinaryTagTypes.STRING;
    }

    @NotNull
    public String value();
}

