/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.autocomplete;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestAnnotationProviderFactory;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestWithProviderFactory;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandParameter;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class SuggestionProviders<A extends CommandActor> {
    private static final List<SuggestionProvider.Factory<?>> DEFAULT_FACTORIES = Arrays.asList(SuggestAnnotationProviderFactory.INSTANCE, SuggestWithProviderFactory.INSTANCE);
    private final List<SuggestionProvider.Factory<? super A>> factories;
    private final int lastIndex;

    private SuggestionProviders(Builder<A> builder) {
        ArrayList<SuggestionProvider.Factory<A>> factories = new ArrayList<SuggestionProvider.Factory<A>>(((Builder)builder).factories.size() + DEFAULT_FACTORIES.size());
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
    public SuggestionProvider<A> provider(@NotNull CommandParameter parameter, @NotNull Lamp<A> lamp) {
        Preconditions.notNull(parameter, "parameter");
        Preconditions.notNull(lamp, "Lamp");
        return this.provider(parameter.fullType(), parameter.annotations(), lamp);
    }

    @NotNull
    public SuggestionProvider<A> provider(@NotNull Type type, @NotNull AnnotationList annotations, @NotNull Lamp<A> lamp) {
        Preconditions.notNull(type, "type");
        Preconditions.notNull(annotations, "annotations");
        Preconditions.notNull(lamp, "Lamp");
        for (SuggestionProvider.Factory<A> factory : this.factories) {
            SuggestionProvider<? super A> provider = factory.create(type, annotations, lamp);
            if (provider == null) continue;
            return provider;
        }
        return SuggestionProvider.empty();
    }

    @NotNull
    public SuggestionProvider<A> findNextProvider(@NotNull Type type, @NotNull AnnotationList annotations, @NotNull SuggestionProvider.Factory<? super A> skipPast, @NotNull Lamp<A> lamp) {
        int skipPastIndex = this.factories.indexOf(skipPast);
        if (skipPastIndex == -1) {
            throw new IllegalArgumentException("Don't know how to skip past unknown provider factory: " + skipPastIndex + " (it isn't registered?)");
        }
        int size = this.factories.size();
        for (int i = skipPastIndex + 1; i < size; ++i) {
            SuggestionProvider.Factory<A> factory = this.factories.get(i);
            SuggestionProvider<? super A> parameterType = factory.create(type, annotations, lamp);
            if (parameterType == null) continue;
            return parameterType;
        }
        return SuggestionProvider.empty();
    }

    @Contract(value="-> new", pure=true)
    @NotNull
    public Builder<A> toBuilder() {
        int i;
        Builder<Object> result = new Builder<Object>();
        for (i = 0; i < this.lastIndex; ++i) {
            result.addProviderFactory(this.factories.get(i));
        }
        int limit = this.factories.size() - DEFAULT_FACTORIES.size();
        for (i = this.lastIndex; i < limit; ++i) {
            result.addProviderFactoryLast(this.factories.get(i));
        }
        return result;
    }

    public static class Builder<A extends CommandActor> {
        private final List<SuggestionProvider.Factory<? super A>> factories = new ArrayList<SuggestionProvider.Factory<? super A>>();
        private int lastIndex = 0;

        @NotNull
        public Builder<A> addProvider(@NotNull Class<?> type, @NotNull SuggestionProvider<A> provider) {
            this.addProviderFactory(SuggestionProvider.Factory.forType(type, provider));
            return this;
        }

        @NotNull
        public Builder<A> addProviderLast(@NotNull Class<?> type, @NotNull SuggestionProvider<A> provider) {
            Preconditions.notNull(type, "type");
            this.addProviderFactoryLast(SuggestionProvider.Factory.forType(type, provider));
            return this;
        }

        @NotNull
        public <L extends Annotation> Builder<A> addProviderForAnnotation(@NotNull Class<L> type, @NotNull Function<L, SuggestionProvider<A>> provider) {
            this.addProviderFactory(SuggestionProvider.Factory.forAnnotation(type, provider));
            return this;
        }

        @NotNull
        public <L extends Annotation> Builder<A> addProviderForAnnotationLast(@NotNull Class<L> type, @NotNull Function<L, SuggestionProvider<A>> provider) {
            this.addProviderFactoryLast(SuggestionProvider.Factory.forAnnotation(type, provider));
            return this;
        }

        public Builder<A> addProviderFactory(@NotNull SuggestionProvider.Factory<? super A> factory) {
            Preconditions.notNull(factory, "factory");
            this.factories.add(this.lastIndex++, factory);
            return this;
        }

        public Builder<A> addProviderFactoryLast(@NotNull SuggestionProvider.Factory<? super A> factory) {
            Preconditions.notNull(factory, "factory");
            this.factories.add(factory);
            return this;
        }

        @Contract(pure=true, value="-> new")
        @NotNull
        public SuggestionProviders<A> build() {
            return new SuggestionProviders(this);
        }
    }
}

