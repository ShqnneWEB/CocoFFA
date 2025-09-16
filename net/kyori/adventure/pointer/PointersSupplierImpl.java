/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.pointer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.kyori.adventure.pointer.Pointer;
import net.kyori.adventure.pointer.Pointers;
import net.kyori.adventure.pointer.PointersSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class PointersSupplierImpl<T>
implements PointersSupplier<T> {
    @Nullable
    private final PointersSupplier<? super T> parent;
    private final Map<Pointer<?>, Function<T, ?>> resolvers;

    PointersSupplierImpl(@NotNull BuilderImpl<T> builder) {
        this.parent = ((BuilderImpl)builder).parent;
        this.resolvers = new HashMap(((BuilderImpl)builder).resolvers);
    }

    @Override
    @NotNull
    public Pointers view(@NotNull T instance) {
        return new ForwardingPointers<T>(instance, this);
    }

    @Override
    public <P> boolean supports(@NotNull Pointer<P> pointer) {
        if (this.resolvers.containsKey(Objects.requireNonNull(pointer, "pointer"))) {
            return true;
        }
        if (this.parent == null) {
            return false;
        }
        return this.parent.supports(pointer);
    }

    @Override
    @Nullable
    public <P> Function<? super T, P> resolver(@NotNull Pointer<P> pointer) {
        Function<T, ?> resolver = this.resolvers.get(Objects.requireNonNull(pointer, "pointer"));
        if (resolver != null) {
            return resolver;
        }
        if (this.parent == null) {
            return null;
        }
        return this.parent.resolver(pointer);
    }

    static final class BuilderImpl<T>
    implements PointersSupplier.Builder<T> {
        @Nullable
        private PointersSupplier<? super T> parent = null;
        private final Map<Pointer<?>, Function<T, ?>> resolvers = new HashMap();

        BuilderImpl() {
        }

        @Override
        @NotNull
        public PointersSupplier.Builder<T> parent(@Nullable PointersSupplier<? super T> parent) {
            this.parent = parent;
            return this;
        }

        @Override
        @NotNull
        public <P> PointersSupplier.Builder<T> resolving(@NotNull Pointer<P> pointer, @NotNull Function<T, P> resolver) {
            this.resolvers.put(pointer, resolver);
            return this;
        }

        @Override
        @NotNull
        public PointersSupplier<T> build() {
            return new PointersSupplierImpl(this);
        }
    }

    static final class ForwardingPointers<U>
    implements Pointers {
        private final U instance;
        private final PointersSupplierImpl<U> supplier;

        ForwardingPointers(@NotNull U instance, @NotNull PointersSupplierImpl<U> supplier) {
            this.instance = instance;
            this.supplier = supplier;
        }

        @Override
        @NotNull
        public <T> Optional<T> get(@NotNull Pointer<T> pointer) {
            PointersSupplier parent;
            Function<U, U> resolver = (Function<U, U>)((PointersSupplierImpl)this.supplier).resolvers.get(Objects.requireNonNull(pointer, "pointer"));
            if (resolver == null && (parent = ((PointersSupplierImpl)this.supplier).parent) != null) {
                resolver = parent.resolver(pointer);
            }
            if (resolver == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(resolver.apply(this.instance));
        }

        @Override
        public <T> boolean supports(@NotNull Pointer<T> pointer) {
            return this.supplier.supports(pointer);
        }

        @Override
        public @NotNull Pointers.Builder toBuilder() {
            Pointers.Builder builder = ((PointersSupplierImpl)this.supplier).parent == null ? Pointers.builder() : (Pointers.Builder)((PointersSupplierImpl)this.supplier).parent.view(this.instance).toBuilder();
            for (Map.Entry entry : ((PointersSupplierImpl)this.supplier).resolvers.entrySet()) {
                builder.withDynamic((Pointer)entry.getKey(), () -> ((Function)entry.getValue()).apply(this.instance));
            }
            return builder;
        }
    }
}

