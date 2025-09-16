/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 */
package hu.geri.libs.revxrsal.commands.brigadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import hu.geri.libs.revxrsal.commands.brigadier.types.ArgumentTypeFactory;
import hu.geri.libs.revxrsal.commands.brigadier.types.BTypeFactory;
import hu.geri.libs.revxrsal.commands.brigadier.types.DefaultTypeFactories;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class ArgumentTypes<A extends CommandActor> {
    private static final List<ArgumentTypeFactory<?>> HIGHEST_PRIORITY = Collections.singletonList(BTypeFactory.INSTANCE);
    private static final List<ArgumentTypeFactory<?>> DEFAULT_FACTORIES = Arrays.asList(DefaultTypeFactories.LONG, DefaultTypeFactories.INTEGER, DefaultTypeFactories.SHORT, DefaultTypeFactories.BYTE, DefaultTypeFactories.DOUBLE, DefaultTypeFactories.FLOAT, DefaultTypeFactories.BOOLEAN, DefaultTypeFactories.CHAR, DefaultTypeFactories.STRING);
    private final List<ArgumentTypeFactory<? super A>> factories;
    private final int lastIndex;

    public ArgumentTypes(@NotNull Builder<A> builder) {
        ArrayList<ArgumentTypeFactory<A>> factories = new ArrayList<ArgumentTypeFactory<A>>(((Builder)builder).factories.size() + DEFAULT_FACTORIES.size());
        factories.addAll(HIGHEST_PRIORITY);
        factories.addAll(((Builder)builder).factories);
        factories.addAll(DEFAULT_FACTORIES);
        this.factories = factories;
        this.lastIndex = ((Builder)builder).lastIndex;
    }

    @Contract(value="-> new", pure=true)
    @NotNull
    public static <A extends CommandActor> Builder<A> builder() {
        return new Builder();
    }

    @NotNull
    public ArgumentType<?> type(@NotNull ParameterNode<A, ?> parameter) {
        Preconditions.notNull(parameter, "parameter");
        for (ArgumentTypeFactory<A> factory : this.factories) {
            ArgumentType<?> provider = factory.getArgumentType(parameter);
            if (provider == null) continue;
            return provider;
        }
        return parameter.isGreedy() ? StringArgumentType.greedyString() : StringArgumentType.string();
    }

    @Contract(value="-> new", pure=true)
    @NotNull
    public Builder<A> toBuilder() {
        int i;
        Builder<Object> result = new Builder<Object>();
        for (i = HIGHEST_PRIORITY.size(); i < this.lastIndex; ++i) {
            result.addTypeFactory(this.factories.get(i));
        }
        int limit = this.factories.size() - DEFAULT_FACTORIES.size();
        for (i = this.lastIndex; i < limit; ++i) {
            result.addTypeFactoryLast(this.factories.get(i));
        }
        return result;
    }

    public static class Builder<A extends CommandActor> {
        private final List<ArgumentTypeFactory<? super A>> factories = new ArrayList<ArgumentTypeFactory<? super A>>();
        private int lastIndex = 0;

        @NotNull
        public Builder<A> addType(Class<?> type, @NotNull ArgumentType<?> argumentType) {
            Preconditions.notNull(type, "type");
            this.addTypeFactory(ArgumentTypeFactory.forType(type, argumentType));
            return this;
        }

        @NotNull
        public Builder<A> addTypeLast(Class<?> type, @NotNull ArgumentType<?> argumentType) {
            Preconditions.notNull(type, "type");
            return this.addTypeFactoryLast(ArgumentTypeFactory.forType(type, argumentType));
        }

        @NotNull
        public Builder<A> addTypeFactory(@NotNull ArgumentTypeFactory<? super A> factory) {
            Preconditions.notNull(factory, "factory");
            this.factories.add(this.lastIndex++, factory);
            return this;
        }

        @NotNull
        public Builder<A> addTypeFactoryLast(@NotNull ArgumentTypeFactory<? super A> factory) {
            Preconditions.notNull(factory, "factory");
            this.factories.add(factory);
            return this;
        }

        @Contract(value="-> new", pure=true)
        @NotNull
        public ArgumentTypes<A> build() {
            return new ArgumentTypes(this);
        }
    }
}

