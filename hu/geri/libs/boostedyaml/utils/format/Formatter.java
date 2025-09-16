/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.utils.format;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.utils.format.NodeRole;
import org.jetbrains.annotations.NotNull;

public interface Formatter<S, V> {
    @NotNull
    public S format(@NotNull Tag var1, @NotNull V var2, @NotNull NodeRole var3, @NotNull S var4);

    @NotNull
    public static <S, V> Formatter<S, V> identity() {
        return (tag, value, role, def) -> def;
    }
}

