/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.autocomplete;

import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

final class EmptySuggestionProvider
implements SuggestionProvider<CommandActor> {
    public static final EmptySuggestionProvider INSTANCE = new EmptySuggestionProvider();

    private EmptySuggestionProvider() {
    }

    @NotNull
    public List<String> getSuggestions(@NotNull ExecutionContext<CommandActor> context) {
        return Collections.emptyList();
    }

    public String toString() {
        return "SuggestionProvider.empty()";
    }

    public int hashCode() {
        return EmptySuggestionProvider.class.hashCode();
    }

    public boolean equals(Object obj) {
        return obj instanceof EmptySuggestionProvider;
    }
}

