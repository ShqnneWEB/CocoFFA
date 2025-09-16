/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.option.value;

import net.kyori.option.Option;
import net.kyori.option.value.ValueSources;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface ValueSource {
    public static ValueSource environmentVariable() {
        return ValueSource.environmentVariable("");
    }

    public static ValueSource environmentVariable(String prefix) {
        return new ValueSources.EnvironmentVariable(prefix);
    }

    public static ValueSource systemProperty() {
        return ValueSource.systemProperty("");
    }

    public static ValueSource systemProperty(String prefix) {
        return new ValueSources.SystemProperty(prefix);
    }

    public <T> @Nullable T value(Option<T> var1);
}

