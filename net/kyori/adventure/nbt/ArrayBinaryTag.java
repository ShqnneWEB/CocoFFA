/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.nbt;

import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagType;
import org.jetbrains.annotations.NotNull;

public interface ArrayBinaryTag
extends BinaryTag {
    @NotNull
    public BinaryTagType<? extends ArrayBinaryTag> type();
}

