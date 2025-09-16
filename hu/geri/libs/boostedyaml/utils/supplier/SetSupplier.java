/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.utils.supplier;

import java.util.Set;
import org.jetbrains.annotations.NotNull;

public interface SetSupplier {
    @NotNull
    public <T> Set<T> supply(int var1);
}

