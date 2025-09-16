/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.flattener;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.flattener.FlattenerListener;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.util.InheritanceAwareMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

final class ComponentFlattenerImpl
implements ComponentFlattener {
    static final ComponentFlattener BASIC = (ComponentFlattener)new BuilderImpl().mapper(KeybindComponent.class, component -> component.keybind()).mapper(ScoreComponent.class, component -> {
        @Nullable String value = component.value();
        return value != null ? value : "";
    }).mapper(SelectorComponent.class, SelectorComponent::pattern).mapper(TextComponent.class, TextComponent::content).mapper(TranslatableComponent.class, component -> {
        @Nullable String fallback = component.fallback();
        return fallback != null ? fallback : component.key();
    }).build();
    static final ComponentFlattener TEXT_ONLY = (ComponentFlattener)new BuilderImpl().mapper(TextComponent.class, TextComponent::content).build();
    private static final int MAX_DEPTH = 512;
    private final InheritanceAwareMap<Component, Handler> flatteners;
    private final Function<Component, String> unknownHandler;
    private final int maxNestedDepth;

    ComponentFlattenerImpl(InheritanceAwareMap<Component, Handler> flatteners, @Nullable Function<Component, String> unknownHandler, int maxNestedDepth) {
        this.flatteners = flatteners;
        this.unknownHandler = unknownHandler;
        this.maxNestedDepth = maxNestedDepth;
    }

    @Override
    public void flatten(@NotNull Component input, @NotNull FlattenerListener listener) {
        this.flatten0(input, listener, 0, 0);
    }

    private void flatten0(@NotNull Component input, @NotNull FlattenerListener listener, int depth, int nestedDepth) {
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(listener, "listener");
        if (input == Component.empty()) {
            return;
        }
        if (this.maxNestedDepth != -1 && nestedDepth > this.maxNestedDepth) {
            throw new IllegalStateException("Exceeded maximum nesting depth of " + this.maxNestedDepth + " while attempting to flatten components!");
        }
        ArrayDeque<StackEntry> componentStack = new ArrayDeque<StackEntry>();
        ArrayDeque<Style> styleStack = new ArrayDeque<Style>();
        componentStack.push(new StackEntry(input, depth));
        while (!componentStack.isEmpty()) {
            StackEntry entry = (StackEntry)componentStack.pop();
            int currentDepth = entry.depth;
            if (currentDepth > 512) {
                throw new IllegalStateException("Exceeded maximum depth of 512 while attempting to flatten components!");
            }
            Component component = entry.component;
            @Nullable Handler flattener = this.flattener(component);
            Style componentStyle = component.style();
            listener.pushStyle(componentStyle);
            styleStack.push(componentStyle);
            if (flattener != null) {
                flattener.handle(this, component, listener, currentDepth, nestedDepth);
            }
            if (!component.children().isEmpty() && listener.shouldContinue()) {
                List<Component> children = component.children();
                for (int i = children.size() - 1; i >= 0; --i) {
                    componentStack.push(new StackEntry(children.get(i), currentDepth + 1));
                }
                continue;
            }
            Style style = (Style)styleStack.pop();
            listener.popStyle(style);
        }
        while (!styleStack.isEmpty()) {
            Style style = (Style)styleStack.pop();
            listener.popStyle(style);
        }
    }

    @Nullable
    private <T extends Component> Handler flattener(T test) {
        Handler flattener = this.flatteners.get(test.getClass());
        if (flattener == null && this.unknownHandler != null) {
            return (self, component, listener, depth, nestedDepth) -> listener.component(this.unknownHandler.apply(component));
        }
        return flattener;
    }

    @Override
    public @NotNull ComponentFlattener.Builder toBuilder() {
        return new BuilderImpl(this.flatteners, this.unknownHandler, this.maxNestedDepth);
    }

    private static final class StackEntry {
        final Component component;
        final int depth;

        StackEntry(Component component, int depth) {
            this.component = component;
            this.depth = depth;
        }
    }

    @FunctionalInterface
    static interface Handler {
        public void handle(ComponentFlattenerImpl var1, Component var2, FlattenerListener var3, int var4, int var5);
    }

    static final class BuilderImpl
    implements ComponentFlattener.Builder {
        private final InheritanceAwareMap.Builder<Component, Handler> flatteners;
        @Nullable
        private Function<Component, String> unknownHandler;
        private int maxNestedDepth = -1;

        BuilderImpl() {
            this.flatteners = InheritanceAwareMap.builder().strict(true);
        }

        BuilderImpl(InheritanceAwareMap<Component, Handler> flatteners, @Nullable Function<Component, String> unknownHandler, int maxNestedDepth) {
            this.flatteners = InheritanceAwareMap.builder(flatteners).strict(true);
            this.unknownHandler = unknownHandler;
            this.maxNestedDepth = maxNestedDepth;
        }

        @Override
        @NotNull
        public ComponentFlattener build() {
            return new ComponentFlattenerImpl((InheritanceAwareMap)this.flatteners.build(), this.unknownHandler, this.maxNestedDepth);
        }

        @Override
        public <T extends Component> @NotNull ComponentFlattener.Builder mapper(@NotNull Class<T> type, @NotNull Function<T, String> converter) {
            this.flatteners.put(type, (self, component, listener, depth, nestedDepth) -> listener.component((String)converter.apply(component)));
            return this;
        }

        @Override
        public <T extends Component> @NotNull ComponentFlattener.Builder complexMapper(@NotNull Class<T> type, @NotNull BiConsumer<T, Consumer<Component>> converter) {
            this.flatteners.put(type, (self, component, listener, depth, nestedDepth) -> converter.accept(component, c -> self.flatten0(c, listener, depth, nestedDepth + 1)));
            return this;
        }

        @Override
        public @NotNull ComponentFlattener.Builder unknownMapper(@Nullable Function<Component, String> converter) {
            this.unknownHandler = converter;
            return this;
        }

        @Override
        @NotNull
        public ComponentFlattener.Builder nestingLimit(@Range(from=1L, to=0x7FFFFFFFL) int limit) {
            if (limit != -1 && limit < 1) {
                throw new IllegalArgumentException("limit must be positive or ComponentFlattener.NO_NESTING_LIMIT");
            }
            this.maxNestedDepth = limit;
            return this;
        }
    }
}

