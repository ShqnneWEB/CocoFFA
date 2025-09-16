/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.option;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.kyori.option.Option;
import net.kyori.option.OptionImpl;
import net.kyori.option.OptionSchema;
import net.kyori.option.OptionState;
import net.kyori.option.OptionStateImpl;
import net.kyori.option.value.ValueType;
import org.jspecify.annotations.Nullable;

final class OptionSchemaImpl
implements OptionSchema {
    final OptionState emptyState;
    final ConcurrentMap<String, Option<?>> options = new ConcurrentHashMap();

    OptionSchemaImpl(@Nullable OptionSchemaImpl parent) {
        if (parent != null) {
            this.options.putAll(parent.options);
        }
        this.emptyState = new OptionStateImpl(this, new IdentityHashMap());
    }

    @Override
    public Set<Option<?>> knownOptions() {
        return Collections.unmodifiableSet(new HashSet(this.options.values()));
    }

    @Override
    public boolean has(Option<?> option) {
        Option own = (Option)this.options.get(option.id());
        return own != null && own.equals(option);
    }

    @Override
    public OptionState.Builder stateBuilder() {
        return new OptionStateImpl.BuilderImpl(this);
    }

    @Override
    public OptionState.VersionedBuilder versionedStateBuilder() {
        return new OptionStateImpl.VersionedBuilderImpl(this);
    }

    @Override
    public OptionState emptyState() {
        return this.emptyState;
    }

    public String toString() {
        return "OptionSchemaImpl{options=" + this.options + '}';
    }

    final class MutableImpl
    implements OptionSchema.Mutable {
        MutableImpl() {
        }

        <T> Option<T> register(String id, ValueType<T> type, @Nullable T defaultValue) {
            OptionImpl<T> ret = new OptionImpl<T>(Objects.requireNonNull(id, "id"), Objects.requireNonNull(type, "type"), defaultValue);
            if (OptionSchemaImpl.this.options.putIfAbsent(id, ret) != null) {
                throw new IllegalStateException("Key " + id + " has already been used. Option keys must be unique within a schema.");
            }
            return ret;
        }

        @Override
        public Option<String> stringOption(String id, @Nullable String defaultValue) {
            return this.register(id, ValueType.stringType(), defaultValue);
        }

        @Override
        public Option<Boolean> booleanOption(String id, boolean defaultValue) {
            return this.register(id, ValueType.booleanType(), defaultValue);
        }

        @Override
        public Option<Integer> intOption(String id, int defaultValue) {
            return this.register(id, ValueType.integerType(), defaultValue);
        }

        @Override
        public Option<Double> doubleOption(String id, double defaultValue) {
            return this.register(id, ValueType.doubleType(), defaultValue);
        }

        @Override
        public <E extends Enum<E>> Option<E> enumOption(String id, Class<E> enumClazz, @Nullable E defaultValue) {
            return this.register(id, ValueType.enumType(enumClazz), defaultValue);
        }

        @Override
        public OptionSchema frozenView() {
            return OptionSchemaImpl.this;
        }

        @Override
        public Set<Option<?>> knownOptions() {
            return OptionSchemaImpl.this.knownOptions();
        }

        @Override
        public boolean has(Option<?> option) {
            return OptionSchemaImpl.this.has(option);
        }

        @Override
        public OptionState.Builder stateBuilder() {
            return OptionSchemaImpl.this.stateBuilder();
        }

        @Override
        public OptionState.VersionedBuilder versionedStateBuilder() {
            return OptionSchemaImpl.this.versionedStateBuilder();
        }

        @Override
        public OptionState emptyState() {
            return OptionSchemaImpl.this.emptyState();
        }

        public String toString() {
            return "MutableImpl{schema=" + OptionSchemaImpl.this + "}";
        }
    }

    static final class Instances {
        static MutableImpl GLOBAL = new OptionSchemaImpl(null).new MutableImpl();

        Instances() {
        }
    }
}

