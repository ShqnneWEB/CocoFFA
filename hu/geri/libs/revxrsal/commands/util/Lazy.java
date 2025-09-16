/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.util;

import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Lazy {
    private Lazy() {
        Preconditions.cannotInstantiate(Lazy.class);
    }

    @NotNull
    public static <T> Supplier<T> of(@NotNull Supplier<T> fetch) {
        Preconditions.notNull(fetch, "fetch supplier");
        return new LazySupplier<T>(fetch);
    }

    static final class LazySupplier<T>
    implements Supplier<T> {
        final Supplier<T> delegate;
        volatile transient boolean initialized;
        @Nullable
        transient T value;

        LazySupplier(Supplier<T> delegate) {
            this.delegate = delegate;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public T get() {
            if (!this.initialized) {
                LazySupplier lazySupplier = this;
                synchronized (lazySupplier) {
                    if (!this.initialized) {
                        T t = this.delegate.get();
                        this.value = t;
                        this.initialized = true;
                        return t;
                    }
                }
            }
            return this.value;
        }

        public String toString() {
            return "Suppliers.lazy(" + (this.initialized ? "<supplier that returned " + this.value + ">" : this.delegate) + ")";
        }
    }
}

