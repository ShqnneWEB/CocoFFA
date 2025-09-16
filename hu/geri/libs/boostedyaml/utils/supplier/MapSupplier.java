/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.utils.supplier;

import java.util.Map;
import org.jetbrains.annotations.NotNull;

public interface MapSupplier {
    @NotNull
    public <K, V> Map<K, V> supply(int var1);
}

