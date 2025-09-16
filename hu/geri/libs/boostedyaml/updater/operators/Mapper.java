/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.updater.operators;

import hu.geri.libs.boostedyaml.block.implementation.Section;
import hu.geri.libs.boostedyaml.route.Route;
import hu.geri.libs.boostedyaml.settings.updater.ValueMapper;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class Mapper {
    public static void apply(@NotNull Section section, @NotNull Map<Route, ValueMapper> mappers) {
        mappers.forEach((route, mapper) -> section.getParent((Route)route).ifPresent(parent -> {
            Route key = Route.fromSingleKey(route.get(route.length() - 1));
            if (!((Map)parent.getStoredValue()).containsKey(key.get(0))) {
                return;
            }
            parent.set(key, mapper.map((Section)parent, key));
        }));
    }
}

