/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.key;

import net.kyori.adventure.key.KeyPattern;
import org.jetbrains.annotations.NotNull;

public interface Namespaced {
    @KeyPattern.Namespace
    @NotNull
    public String namespace();
}

