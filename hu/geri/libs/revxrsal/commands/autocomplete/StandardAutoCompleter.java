/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.autocomplete;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.autocomplete.AutoCompleter;
import hu.geri.libs.revxrsal.commands.autocomplete.SingleCommandCompleter;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import org.jetbrains.annotations.NotNull;

final class StandardAutoCompleter<A extends CommandActor>
implements AutoCompleter<A> {
    private final Lamp<A> lamp;

    public StandardAutoCompleter(Lamp<A> lamp) {
        this.lamp = lamp;
    }

    @Override
    @NotNull
    public List<String> complete(@NotNull A actor, @NotNull String input) {
        return this.complete(actor, StringStream.create(input));
    }

    @Override
    @NotNull
    public List<String> complete(@NotNull A actor, @NotNull StringStream input) {
        LinkedHashSet<String> suggestions = new LinkedHashSet<String>();
        if (input.isEmpty()) {
            return Collections.emptyList();
        }
        String firstWord = input.peekUnquotedString();
        for (ExecutableCommand<A> possible : this.lamp.registry().commands()) {
            if (possible.isSecret() || !possible.firstNode().name().startsWith(firstWord) || !possible.permission().isExecutableBy(actor)) continue;
            suggestions.addAll(this.complete(possible, input.toMutableCopy(), actor));
        }
        return new ArrayList<String>(suggestions);
    }

    private List<String> complete(ExecutableCommand<A> possible, MutableStringStream input, A actor) {
        SingleCommandCompleter<A> commandCompleter = new SingleCommandCompleter<A>(actor, possible, input);
        commandCompleter.complete();
        return commandCompleter.suggestions();
    }
}

