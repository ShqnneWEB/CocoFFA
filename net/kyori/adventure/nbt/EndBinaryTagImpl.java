/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.nbt;

import net.kyori.adventure.nbt.AbstractBinaryTag;
import net.kyori.adventure.nbt.EndBinaryTag;

final class EndBinaryTagImpl
extends AbstractBinaryTag
implements EndBinaryTag {
    static final EndBinaryTagImpl INSTANCE = new EndBinaryTagImpl();

    EndBinaryTagImpl() {
    }

    public boolean equals(Object that) {
        return this == that;
    }

    public int hashCode() {
        return 0;
    }
}

