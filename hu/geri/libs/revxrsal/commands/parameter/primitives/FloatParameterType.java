/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter.primitives;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import org.jetbrains.annotations.NotNull;

public final class FloatParameterType
implements ParameterType<CommandActor, Float> {
    @Override
    public Float parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<CommandActor> context) {
        return Float.valueOf(input.readFloat());
    }
}

