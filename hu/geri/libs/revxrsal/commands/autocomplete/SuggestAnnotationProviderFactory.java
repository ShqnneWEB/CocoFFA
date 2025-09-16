/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.autocomplete;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.Suggest;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum SuggestAnnotationProviderFactory implements SuggestionProvider.Factory<CommandActor>
{
    INSTANCE;


    @Override
    @Nullable
    public SuggestionProvider<CommandActor> create(@NotNull Type type, @NotNull AnnotationList annotations, @NotNull Lamp<CommandActor> lamp) {
        Suggest suggest = annotations.get(Suggest.class);
        if (suggest == null) {
            return null;
        }
        return SuggestionProvider.of(suggest.value());
    }
}

