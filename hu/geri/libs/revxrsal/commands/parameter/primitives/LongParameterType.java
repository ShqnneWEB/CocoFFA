/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter.primitives;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.parameter.PrioritySpec;
import hu.geri.libs.revxrsal.commands.parameter.primitives.DoubleParameterType;
import hu.geri.libs.revxrsal.commands.parameter.primitives.FloatParameterType;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import org.jetbrains.annotations.NotNull;

public final class LongParameterType
implements ParameterType<CommandActor, Long> {
    private static final PrioritySpec PRIORITY = PrioritySpec.builder().higherThan(DoubleParameterType.class).higherThan(FloatParameterType.class).build();

    @Override
    public Long parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<CommandActor> context) {
        return input.readLong();
    }

    @Override
    @NotNull
    public PrioritySpec parsePriority() {
        return PRIORITY;
    }
}

