/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.autocomplete;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.autocomplete.StandardAutoCompleter;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface AutoCompleter<A extends CommandActor> {
    @ApiStatus.Internal
    @NotNull
    public static <A extends CommandActor> AutoCompleter<A> create(@NotNull Lamp<A> lamp) {
        return new StandardAutoCompleter<A>(lamp);
    }

    @NotNull
    public List<String> complete(@NotNull A var1, @NotNull String var2);

    @NotNull
    public List<String> complete(@NotNull A var1, @NotNull StringStream var2);
}

