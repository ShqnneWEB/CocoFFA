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
import hu.geri.libs.revxrsal.commands.parameter.primitives.IntParameterType;
import hu.geri.libs.revxrsal.commands.parameter.primitives.LongParameterType;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import org.jetbrains.annotations.NotNull;

public final class ShortParameterType
implements ParameterType<CommandActor, Short> {
    private static final PrioritySpec PRIORITY = PrioritySpec.builder().higherThan(DoubleParameterType.class).higherThan(FloatParameterType.class).higherThan(LongParameterType.class).higherThan(IntParameterType.class).build();

    @Override
    public Short parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<CommandActor> context) {
        return input.readShort();
    }

    @Override
    @NotNull
    public PrioritySpec parsePriority() {
        return PRIORITY;
    }
}

