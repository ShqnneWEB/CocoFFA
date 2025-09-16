/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.option;

import java.util.Map;
import java.util.function.Consumer;
import net.kyori.option.Option;
import net.kyori.option.OptionSchema;
import net.kyori.option.value.ValueSource;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
public interface OptionState {
    @Deprecated
    public static OptionState emptyOptionState() {
        return OptionSchema.globalSchema().emptyState();
    }

    @Deprecated
    public static Builder optionState() {
        return OptionSchema.globalSchema().stateBuilder();
    }

    @Deprecated
    public static VersionedBuilder versionedOptionState() {
        return OptionSchema.globalSchema().versionedStateBuilder();
    }

    public OptionSchema schema();

    public boolean has(Option<?> var1);

    public <V> @Nullable V value(Option<V> var1);

    @ApiStatus.NonExtendable
    public static interface Builder {
        public <V> Builder value(Option<V> var1, @Nullable V var2);

        public Builder values(OptionState var1);

        public Builder values(ValueSource var1);

        public OptionState build();
    }

    @ApiStatus.NonExtendable
    public static interface VersionedBuilder {
        public VersionedBuilder version(int var1, Consumer<Builder> var2);

        public Versioned build();
    }

    @ApiStatus.NonExtendable
    public static interface Versioned
    extends OptionState {
        public Map<Integer, OptionState> childStates();

        public Versioned at(int var1);
    }
}

