/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.serialization;

import hu.geri.libs.boostedyaml.utils.supplier.MapSupplier;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface YamlSerializer {
    @Nullable
    public Object deserialize(@NotNull Map<Object, Object> var1);

    @Nullable
    public <T> Map<Object, Object> serialize(@NotNull T var1, @NotNull MapSupplier var2);

    @NotNull
    public Set<Class<?>> getSupportedClasses();

    @NotNull
    public Set<Class<?>> getSupportedParentClasses();
}

