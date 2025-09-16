/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter.primitives;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.parameter.PrioritySpec;
import hu.geri.libs.revxrsal.commands.parameter.StringParameterType;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import org.jetbrains.annotations.NotNull;

public final class CharParameterType
implements ParameterType<CommandActor, Character> {
    private static final PrioritySpec PRIORITY = PrioritySpec.lowest().toBuilder().higherThan(StringParameterType.class).build();

    @Override
    public Character parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<CommandActor> context) {
        return Character.valueOf(input.read());
    }

    @Override
    @NotNull
    public PrioritySpec parsePriority() {
        return PRIORITY;
    }
}

