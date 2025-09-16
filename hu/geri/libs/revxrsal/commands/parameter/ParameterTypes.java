/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.help.Help;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ContextParameter;
import hu.geri.libs.revxrsal.commands.parameter.ParameterFactory;
import hu.geri.libs.revxrsal.commands.parameter.ParameterResolver;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.parameter.StringParameterType;
import hu.geri.libs.revxrsal.commands.parameter.builtins.ArrayParameterTypeFactory;
import hu.geri.libs.revxrsal.commands.parameter.builtins.EnumParameterTypeFactory;
import hu.geri.libs.revxrsal.commands.parameter.builtins.ListParameterTypeFactory;
import hu.geri.libs.revxrsal.commands.parameter.builtins.OptionalParameterTypeFactory;
import hu.geri.libs.revxrsal.commands.parameter.builtins.ParseWithParameterTypeFactory;
import hu.geri.libs.revxrsal.commands.parameter.builtins.SetParameterTypeFactory;
import hu.geri.libs.revxrsal.commands.parameter.builtins.ValuesParameterTypeFactory;
import hu.geri.libs.revxrsal.commands.parameter.primitives.BooleanParameterType;
import hu.geri.libs.revxrsal.commands.parameter.primitives.ByteParameterType;
import hu.geri.libs.revxrsal.commands.parameter.primitives.CharParameterType;
import hu.geri.libs.revxrsal.commands.parameter.primitives.DoubleParameterType;
import hu.geri.libs.revxrsal.commands.parameter.primitives.FloatParameterType;
import hu.geri.libs.revxrsal.commands.parameter.primitives.IntParameterType;
import hu.geri.libs.revxrsal.commands.parameter.primitives.LongParameterType;
import hu.geri.libs.revxrsal.commands.parameter.primitives.ShortParameterType;
import hu.geri.libs.revxrsal.commands.parameter.primitives.UUIDParameterType;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ParameterTypes<A extends CommandActor> {
    private static final List<ParameterFactory> HIGHEST_PRIORITY_FACTORIES = Collections.singletonList(ParseWithParameterTypeFactory.INSTANCE);
    private static final List<ParameterFactory> DEFAULT_FACTORIES = Arrays.asList(ArrayParameterTypeFactory.INSTANCE, ListParameterTypeFactory.INSTANCE, SetParameterTypeFactory.INSTANCE, EnumParameterTypeFactory.INSTANCE, ValuesParameterTypeFactory.INSTANCE, OptionalParameterTypeFactory.INSTANCE, ParameterType.Factory.forType(Integer.TYPE, new IntParameterType()), ParameterType.Factory.forType(Double.TYPE, new DoubleParameterType()), ParameterType.Factory.forType(Long.TYPE, new LongParameterType()), ParameterType.Factory.forType(Float.TYPE, new FloatParameterType()), ParameterType.Factory.forType(Byte.TYPE, new ByteParameterType()), ParameterType.Factory.forType(Short.TYPE, new ShortParameterType()), ParameterType.Factory.forType(Character.TYPE, new CharParameterType()), ParameterType.Factory.forType(Boolean.TYPE, new BooleanParameterType()), ParameterType.Factory.forType(UUID.class, new UUIDParameterType()), ParameterType.Factory.forType(String.class, StringParameterType.single()), ContextParameter.Factory.forType(StringStream.class, (parameter, context) -> context.input()), ContextParameter.Factory.forType(ExecutableCommand.class, (parameter, context) -> context.command()), ContextParameter.Factory.forType(Lamp.class, (parameter, context) -> context.lamp()), ContextParameter.Factory.forType(ExecutionContext.class, (parameter, context) -> context), ContextParameter.Factory.forTypeAndSubclasses(CommandActor.class, (parameter, context) -> context.actor()), ContextParameter.Factory.forType(Help.RelatedCommands.class, (parameter, context) -> context.command().relatedCommands(context.actor())), ContextParameter.Factory.forType(Help.SiblingCommands.class, (parameter, context) -> context.command().siblingCommands(context.actor())), ContextParameter.Factory.forType(Help.ChildrenCommands.class, (parameter, context) -> context.command().childrenCommands(context.actor())));
    private final List<ParameterFactory> factories;
    private final int lastIndex;

    private ParameterTypes(@NotNull Builder<A> builder) {
        ArrayList<ParameterFactory> factories = new ArrayList<ParameterFactory>(((Builder)builder).factories.size() + DEFAULT_FACTORIES.size());
        factories.addAll(HIGHEST_PRIORITY_FACTORIES);
        factories.addAll(((Builder)builder).factories);
        factories.addAll(DEFAULT_FACTORIES);
        this.factories = factories;
        this.lastIndex = ((Builder)builder).lastIndex;
    }

    private static boolean consumesInput(@NotNull ParameterFactory factory) {
        return factory instanceof ParameterType.Factory;
    }

    @Nullable
    private static <A extends CommandActor, T> ParameterResolver<A, T> toParameterResolver(Type type, AnnotationList annotations, Lamp<A> lamp, ParameterFactory factory) {
        ContextParameter contextParameter;
        if (factory instanceof ParameterType.Factory) {
            ParameterType parameterType = ((ParameterType.Factory)factory).create(type, annotations, lamp);
            if (parameterType != null) {
                return ParameterResolver.parameterType(parameterType);
            }
        } else if (factory instanceof ContextParameter.Factory && (contextParameter = ((ContextParameter.Factory)factory).create(type, annotations, lamp)) != null) {
            return ParameterResolver.contextParameter(contextParameter);
        }
        return null;
    }

    @Contract(value="-> new", pure=true)
    @NotNull
    public static <A extends CommandActor> Builder<A> builder() {
        return new Builder();
    }

    @NotNull
    public <T> ParameterResolver<A, T> resolver(Type type, AnnotationList annotations, Lamp<A> lamp) {
        for (ParameterFactory factory : this.factories) {
            ParameterResolver<A, T> parameterType = ParameterTypes.toParameterResolver(type, annotations, lamp, factory);
            if (parameterType == null) continue;
            return parameterType;
        }
        throw new IllegalArgumentException("Failed to find a parameter resolver for type " + type);
    }

    public <T> ParameterResolver<A, T> findNextResolver(Type type, AnnotationList annotations, ParameterFactory skipPast, Lamp<A> lamp) {
        int skipPastIndex = this.factories.indexOf(skipPast);
        if (skipPastIndex == -1) {
            throw new IllegalArgumentException("Don't know how to skip past unknown resolver factory: " + skipPastIndex + " (it isn't registered?)");
        }
        int size = this.factories.size();
        for (int i = skipPastIndex + 1; i < size; ++i) {
            ParameterResolver<A, T> parameterType;
            ParameterFactory factory = this.factories.get(i);
            if (ParameterTypes.consumesInput(skipPast) != ParameterTypes.consumesInput(factory) || (parameterType = ParameterTypes.toParameterResolver(type, annotations, lamp, factory)) == null) continue;
            return parameterType;
        }
        throw new IllegalArgumentException("Failed to find the next resolver for type " + type + " with annotations " + annotations);
    }

    @Contract(value="-> new", pure=true)
    @NotNull
    public Builder<A> toBuilder() {
        int i;
        Builder result = new Builder();
        for (i = HIGHEST_PRIORITY_FACTORIES.size(); i < this.lastIndex; ++i) {
            result.addFactory(this.factories.get(i));
        }
        int limit = this.factories.size() - DEFAULT_FACTORIES.size();
        for (i = this.lastIndex; i < limit; ++i) {
            result.addFactoryLast(this.factories.get(i));
        }
        return result;
    }

    public static class Builder<A extends CommandActor> {
        private final List<ParameterFactory> factories = new ArrayList<ParameterFactory>();
        private int lastIndex = 0;

        public <T> Builder<A> addParameterType(@NotNull Class<T> parameterClass, @NotNull ParameterType<? super A, T> type) {
            return this.addFactory(ParameterType.Factory.forType(parameterClass, type));
        }

        public <T> Builder<A> addParameterTypeLast(@NotNull Class<T> parameterClass, @NotNull ParameterType<? super A, T> parameterType) {
            return this.addFactoryLast(ParameterType.Factory.forType(parameterClass, parameterType));
        }

        public Builder<A> addParameterTypeFactory(@NotNull ParameterType.Factory<? super A> factory) {
            return this.addFactory(factory);
        }

        public Builder<A> addParameterTypeFactoryLast(@NotNull ParameterType.Factory<? super A> factory) {
            return this.addFactoryLast(factory);
        }

        public <T> Builder<A> addContextParameter(@NotNull Class<T> parameterClass, @NotNull ContextParameter<? super A, T> type) {
            return this.addFactory(ContextParameter.Factory.forType(parameterClass, type));
        }

        public <T> Builder<A> addContextParameterLast(@NotNull Class<T> parameterClass, @NotNull ContextParameter<? super A, T> contextParameter) {
            return this.addFactoryLast(ContextParameter.Factory.forType(parameterClass, contextParameter));
        }

        public Builder<A> addContextParameterFactory(@NotNull ContextParameter.Factory<? super A> factory) {
            return this.addFactory(factory);
        }

        public Builder<A> addContextParameterFactoryLast(@NotNull ContextParameter.Factory<? super A> factory) {
            return this.addFactoryLast(factory);
        }

        private Builder<A> addFactory(@NotNull ParameterFactory factory) {
            this.factories.add(this.lastIndex++, factory);
            return this;
        }

        private Builder<A> addFactoryLast(@NotNull ParameterFactory factory) {
            this.factories.add(factory);
            return this;
        }

        @Contract(pure=true, value="-> new")
        @NotNull
        public ParameterTypes<A> build() {
            return new ParameterTypes(this);
        }
    }
}

