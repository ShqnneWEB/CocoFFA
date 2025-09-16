/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.autocomplete;

import hu.geri.libs.revxrsal.commands.autocomplete.AsyncSuggestionProvider;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

final class WrapperAsyncSuggestionProvider<A extends CommandActor>
implements SuggestionProvider<A>,
AsyncSuggestionProvider<A> {
    @NotNull
    private final AsyncSuggestionProvider<A> provider;

    public WrapperAsyncSuggestionProvider(@NotNull AsyncSuggestionProvider<A> provider) {
        this.provider = provider;
    }

    @Override
    @NotNull
    public Collection<String> getSuggestions(@NotNull ExecutionContext<A> context) {
        return this.provider.getSuggestionsAsync(context).get();
    }

    @Override
    @NotNull
    public CompletableFuture<Collection<String>> getSuggestionsAsync(@NotNull ExecutionContext<A> context) {
        return this.provider.getSuggestionsAsync(context);
    }
}

