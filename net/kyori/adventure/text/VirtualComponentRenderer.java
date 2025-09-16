/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text;

import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public interface VirtualComponentRenderer<C> {
    public @UnknownNullability ComponentLike apply(@NotNull C var1);

    @NotNull
    default public String fallbackString() {
        return "";
    }
}

