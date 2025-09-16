/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.flattener;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattenerImpl;
import net.kyori.adventure.text.flattener.FlattenerListener;
import net.kyori.adventure.util.Buildable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public interface ComponentFlattener
extends Buildable<ComponentFlattener, Builder> {
    public static final int NO_NESTING_LIMIT = -1;

    @NotNull
    public static Builder builder() {
        return new ComponentFlattenerImpl.BuilderImpl();
    }

    @NotNull
    public static ComponentFlattener basic() {
        return ComponentFlattenerImpl.BASIC;
    }

    @NotNull
    public static ComponentFlattener textOnly() {
        return ComponentFlattenerImpl.TEXT_ONLY;
    }

    public void flatten(@NotNull Component var1, @NotNull FlattenerListener var2);

    public static interface Builder
    extends AbstractBuilder<ComponentFlattener>,
    Buildable.Builder<ComponentFlattener> {
        @NotNull
        public <T extends Component> Builder mapper(@NotNull Class<T> var1, @NotNull Function<T, String> var2);

        @NotNull
        public <T extends Component> Builder complexMapper(@NotNull Class<T> var1, @NotNull BiConsumer<T, Consumer<Component>> var2);

        @NotNull
        public Builder unknownMapper(@Nullable Function<Component, String> var1);

        @NotNull
        public Builder nestingLimit(@Range(from=1L, to=0x7FFFFFFFL) int var1);
    }
}

