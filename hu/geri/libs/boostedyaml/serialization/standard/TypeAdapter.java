/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.serialization.standard;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public interface TypeAdapter<T> {
    @NotNull
    public Map<Object, Object> serialize(@NotNull T var1);

    @NotNull
    public T deserialize(@NotNull Map<Object, Object> var1);

    @NotNull
    default public Map<String, Object> toStringKeyedMap(@NotNull Map<?, ?> map) {
        HashMap<String, Object> newMap = new HashMap<String, Object>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                newMap.put(entry.getKey().toString(), this.toStringKeyedMap((Map)entry.getValue()));
                continue;
            }
            newMap.put(entry.getKey().toString(), entry.getValue());
        }
        return newMap;
    }
}

