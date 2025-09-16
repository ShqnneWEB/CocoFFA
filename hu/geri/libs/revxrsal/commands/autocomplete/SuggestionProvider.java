/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.autocomplete;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.autocomplete.AsyncSuggestionProvider;
import hu.geri.libs.revxrsal.commands.autocomplete.BaseSuggestionProvider;
import hu.geri.libs.revxrsal.commands.autocomplete.ClassSuggestionProviderFactory;
import hu.geri.libs.revxrsal.commands.autocomplete.EmptySuggestionProvider;
import hu.geri.libs.revxrsal.commands.autocomplete.WrapperAsyncSuggestionProvider;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.util.Classes;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface SuggestionProvider<A extends CommandActor>
extends BaseSuggestionProvider {
    @NotNull
    public static <A extends CommandActor> SuggestionProvider<A> empty() {
        return EmptySuggestionProvider.INSTANCE;
    }

    @Contract(value="_ -> new")
    @NotNull
    public static <A extends CommandActor> SuggestionProvider<A> fromAsync(@NotNull AsyncSuggestionProvider<A> provider) {
        return new WrapperAsyncSuggestionProvider<A>(provider);
    }

    @NotNull
    public static <A extends CommandActor> SuggestionProvider<A> of(@NotNull String ... suggestions) {
        if (suggestions == null || suggestions.length == 0) {
            return SuggestionProvider.empty();
        }
        List<String> list = Arrays.asList(suggestions);
        return context -> list;
    }

    @NotNull
    public static <A extends CommandActor> SuggestionProvider<A> of(@NotNull List<String> suggestions) {
        if (suggestions.isEmpty()) {
            return SuggestionProvider.empty();
        }
        return context -> suggestions;
    }

    @NotNull
    public Collection<String> getSuggestions(@NotNull ExecutionContext<A> var1);

    public static interface Factory<A extends CommandActor>
    extends BaseSuggestionProvider {
        public static <A extends CommandActor> Factory<? super A> forType(@NotNull Class<?> type, @NotNull SuggestionProvider<A> provider) {
            return new ClassSuggestionProviderFactory<A>(type, provider, false);
        }

        public static <A extends CommandActor> Factory<? super A> forTypeAndSubclasses(@NotNull Class<?> type, @NotNull SuggestionProvider<A> provider) {
            return new ClassSuggestionProviderFactory<A>(type, provider, true);
        }

        @NotNull
        public static <A extends CommandActor, L extends Annotation> @NotNull @NotNull Factory<? super A> forAnnotation(@NotNull Class<L> annotationType, @NotNull Function<L, SuggestionProvider<A>> provider) {
            Classes.checkRetention(annotationType);
            return (type, annotations, lamp) -> {
                Object annotation = annotations.get(annotationType);
                if (annotation != null) {
                    return (SuggestionProvider)provider.apply(annotation);
                }
                return null;
            };
        }

        @Nullable
        public SuggestionProvider<A> create(@NotNull Type var1, @NotNull AnnotationList var2, @NotNull Lamp<A> var3);
    }
}

