/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.utils.supplier;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface ListSupplier {
    @NotNull
    public <T> List<T> supply(int var1);
}

