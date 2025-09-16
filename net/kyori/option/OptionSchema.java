/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.option;

import java.util.Objects;
import java.util.Set;
import net.kyori.option.Option;
import net.kyori.option.OptionSchemaImpl;
import net.kyori.option.OptionState;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
public interface OptionSchema {
    public static Mutable globalSchema() {
        return OptionSchemaImpl.Instances.GLOBAL;
    }

    public static Mutable childSchema(OptionSchema schema) {
        OptionSchemaImpl impl = schema instanceof OptionSchemaImpl.MutableImpl ? (OptionSchemaImpl)((Mutable)schema).frozenView() : (OptionSchemaImpl)schema;
        return new OptionSchemaImpl.MutableImpl(new OptionSchemaImpl(Objects.requireNonNull(impl, "impl")));
    }

    public static Mutable emptySchema() {
        return new OptionSchemaImpl.MutableImpl(new OptionSchemaImpl(null));
    }

    public Set<Option<?>> knownOptions();

    public boolean has(Option<?> var1);

    public OptionState.Builder stateBuilder();

    public OptionState.VersionedBuilder versionedStateBuilder();

    public OptionState emptyState();

    @ApiStatus.NonExtendable
    public static interface Mutable
    extends OptionSchema {
        public Option<String> stringOption(String var1, @Nullable String var2);

        public Option<Boolean> booleanOption(String var1, boolean var2);

        public Option<Integer> intOption(String var1, int var2);

        public Option<Double> doubleOption(String var1, double var2);

        public <E extends Enum<E>> Option<E> enumOption(String var1, Class<E> var2, @Nullable E var3);

        public OptionSchema frozenView();
    }
}

