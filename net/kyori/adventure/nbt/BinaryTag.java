/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.nbt;

import net.kyori.adventure.nbt.BinaryTagLike;
import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.examination.Examinable;
import org.jetbrains.annotations.NotNull;

public interface BinaryTag
extends BinaryTagLike,
Examinable {
    @NotNull
    public BinaryTagType<? extends BinaryTag> type();

    @Override
    @NotNull
    default public BinaryTag asBinaryTag() {
        return this;
    }
}

