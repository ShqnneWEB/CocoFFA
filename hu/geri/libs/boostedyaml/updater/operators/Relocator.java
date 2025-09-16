/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.updater.operators;

import hu.geri.libs.boostedyaml.block.Block;
import hu.geri.libs.boostedyaml.block.implementation.Section;
import hu.geri.libs.boostedyaml.route.Route;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Relocator {
    private static final Relocator INSTANCE = new Relocator();

    public static void apply(@NotNull Section section, @NotNull Map<Route, Route> relocations) {
        while (relocations.size() > 0) {
            INSTANCE.apply(section, relocations, relocations.keySet().iterator().next());
        }
    }

    private void apply(@NotNull Section section, @NotNull Map<Route, Route> relocations, @Nullable Route from) {
        if (from == null || !relocations.containsKey(from)) {
            return;
        }
        Optional<Section> parent = section.getParent(from);
        if (!parent.isPresent()) {
            relocations.remove(from);
            return;
        }
        Object lastKey = from.get(from.length() - 1);
        Block block = (Block)((Map)parent.get().getStoredValue()).get(lastKey);
        if (block == null) {
            relocations.remove(from);
            return;
        }
        Route to = relocations.get(from);
        relocations.remove(from);
        ((Map)parent.get().getStoredValue()).remove(lastKey);
        this.removeParents(parent.get());
        this.apply(section, relocations, to);
        section.set(to, (Object)block);
    }

    private void removeParents(@NotNull Section section) {
        if (section.isEmpty(false) && !section.isRoot()) {
            ((Map)section.getParent().getStoredValue()).remove(section.getName());
            this.removeParents(section.getParent());
        }
    }
}

