/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.reflect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MethodCaller {
    public Object call(@Nullable Object var1, Object ... var2);

    default public BoundMethodCaller bindTo(@Nullable Object instance) {
        return arguments -> this.call(instance, arguments);
    }

    public static interface BoundMethodCaller {
        public Object call(@NotNull Object ... var1);
    }
}

