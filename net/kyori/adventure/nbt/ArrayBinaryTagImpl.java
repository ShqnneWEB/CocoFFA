/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.nbt;

import net.kyori.adventure.nbt.AbstractBinaryTag;
import net.kyori.adventure.nbt.ArrayBinaryTag;

abstract class ArrayBinaryTagImpl
extends AbstractBinaryTag
implements ArrayBinaryTag {
    ArrayBinaryTagImpl() {
    }

    static void checkIndex(int index, int length) {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }
}

