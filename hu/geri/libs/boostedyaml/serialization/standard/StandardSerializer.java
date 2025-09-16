/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.serialization.standard;

import hu.geri.libs.boostedyaml.serialization.YamlSerializer;
import hu.geri.libs.boostedyaml.serialization.standard.TypeAdapter;
import hu.geri.libs.boostedyaml.utils.supplier.MapSupplier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StandardSerializer
implements YamlSerializer {
    public static final String DEFAULT_SERIALIZED_TYPE_KEY = "==";
    private static final StandardSerializer defaultSerializer = new StandardSerializer("==");
    private final Map<Class<?>, TypeAdapter<?>> adapters = new HashMap();
    private final Map<String, Class<?>> aliases = new HashMap();
    private final Object serializedTypeKey;

    public StandardSerializer(@NotNull Object serializedTypeKey) {
        this.serializedTypeKey = serializedTypeKey;
    }

    public <T> void register(@NotNull Class<T> clazz, @NotNull TypeAdapter<T> adapter) {
        this.adapters.put(clazz, adapter);
        this.aliases.put(clazz.getCanonicalName(), clazz);
    }

    public <T> void register(@NotNull String alias, @NotNull Class<T> clazz) {
        if (!this.adapters.containsKey(clazz)) {
            throw new IllegalStateException("Cannot register an alias for yet unregistered type!");
        }
        this.aliases.put(alias, clazz);
    }

    @Override
    @Nullable
    public Object deserialize(@NotNull Map<Object, Object> map) {
        if (!map.containsKey(this.serializedTypeKey)) {
            return null;
        }
        Class<?> type = this.aliases.get(map.get(this.serializedTypeKey).toString());
        if (type == null) {
            return null;
        }
        return this.adapters.get(type).deserialize(map);
    }

    @Override
    @Nullable
    public <T> Map<Object, Object> serialize(@NotNull T object, @NotNull MapSupplier supplier) {
        if (!this.adapters.containsKey(object.getClass())) {
            return null;
        }
        Map<Object, Object> serialized = supplier.supply(1);
        serialized.putAll(this.adapters.get(object.getClass()).serialize(object));
        serialized.computeIfAbsent(this.serializedTypeKey, k -> object.getClass().getCanonicalName());
        return serialized;
    }

    @Override
    @NotNull
    public Set<Class<?>> getSupportedClasses() {
        return this.adapters.keySet();
    }

    @Override
    @NotNull
    public Set<Class<?>> getSupportedParentClasses() {
        return Collections.emptySet();
    }

    public static StandardSerializer getDefault() {
        return defaultSerializer;
    }
}

