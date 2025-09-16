/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.autocomplete;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.SuggestWith;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.autocomplete.AsyncSuggestionProvider;
import hu.geri.libs.revxrsal.commands.autocomplete.BaseSuggestionProvider;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.util.InstanceCreator;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum SuggestWithProviderFactory implements SuggestionProvider.Factory<CommandActor>
{
    INSTANCE;


    @Override
    @Nullable
    public SuggestionProvider<CommandActor> create(@NotNull Type parameterType, @NotNull AnnotationList annotations, @NotNull Lamp<CommandActor> lamp) {
        SuggestWith suggestWith = annotations.get(SuggestWith.class);
        if (suggestWith == null) {
            return null;
        }
        BaseSuggestionProvider type = InstanceCreator.create(suggestWith.value());
        if (type instanceof SuggestionProvider) {
            SuggestionProvider pType = (SuggestionProvider)type;
            return pType;
        }
        if (type instanceof SuggestionProvider.Factory) {
            SuggestionProvider.Factory factory = (SuggestionProvider.Factory)type;
            return factory.create(parameterType, annotations, lamp);
        }
        if (type instanceof AsyncSuggestionProvider) {
            AsyncSuggestionProvider async = (AsyncSuggestionProvider)type;
            return SuggestionProvider.fromAsync(async);
        }
        throw new IllegalArgumentException("Don't know how to create a SuggestionProvider from " + type);
    }
}

