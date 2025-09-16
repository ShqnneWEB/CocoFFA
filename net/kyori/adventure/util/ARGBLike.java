/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.util;

import net.kyori.adventure.util.RGBLike;
import org.jetbrains.annotations.Range;

public interface ARGBLike
extends RGBLike {
    public @Range(from=0L, to=255L) int alpha();
}

