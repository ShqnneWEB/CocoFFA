/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.settings.updater;

import hu.geri.libs.boostedyaml.block.Block;
import hu.geri.libs.boostedyaml.block.implementation.Section;
import hu.geri.libs.boostedyaml.route.Route;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ValueMapper {
    @Nullable
    default public Object map(@NotNull Section section, @NotNull Route key) {
        return this.map(section.getBlock(key));
    }

    @Nullable
    default public Object map(@NotNull Block<?> block) {
        return this.map(block.getStoredValue());
    }

    @Nullable
    default public Object map(@Nullable Object value) {
        return value;
    }

    public static ValueMapper section(final BiFunction<Section, Route, Object> mapper) {
        return new ValueMapper(){

            @Override
            public Object map(@NotNull Section section, @NotNull Route key) {
                return mapper.apply(section, key);
            }
        };
    }

    public static ValueMapper block(final Function<Block<?>, Object> mapper) {
        return new ValueMapper(){

            @Override
            public Object map(@NotNull Block<?> block) {
                return mapper.apply(block);
            }
        };
    }

    public static ValueMapper value(final Function<Object, Object> mapper) {
        return new ValueMapper(){

            @Override
            public Object map(@Nullable Object value) {
                return mapper.apply(value);
            }
        };
    }
}

