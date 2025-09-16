/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter.primitives;

import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.parameter.PrioritySpec;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import org.jetbrains.annotations.NotNull;

public final class BooleanParameterType
implements ParameterType<CommandActor, Boolean> {
    private static final SuggestionProvider<CommandActor> SUGGESTIONS = SuggestionProvider.of("true", "false");

    @Override
    public Boolean parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<CommandActor> context) {
        return input.readBoolean();
    }

    @Override
    @NotNull
    public SuggestionProvider<CommandActor> defaultSuggestions() {
        return SUGGESTIONS;
    }

    @Override
    @NotNull
    public PrioritySpec parsePriority() {
        return PrioritySpec.highest();
    }
}

