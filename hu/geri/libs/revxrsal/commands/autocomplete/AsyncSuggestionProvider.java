/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.autocomplete;

import hu.geri.libs.revxrsal.commands.autocomplete.BaseSuggestionProvider;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface AsyncSuggestionProvider<A extends CommandActor>
extends BaseSuggestionProvider {
    @NotNull
    public static <A extends CommandActor> AsyncSuggestionProvider<A> from(@NotNull SuggestionProvider<A> provider) {
        return context -> CompletableFuture.supplyAsync(() -> provider.getSuggestions(context));
    }

    @NotNull
    public CompletableFuture<Collection<String>> getSuggestionsAsync(@NotNull ExecutionContext<A> var1);
}

