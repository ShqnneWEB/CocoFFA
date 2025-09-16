/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.Range;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.NumberNotInRangeException;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.process.ParameterValidator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public enum RangeChecker implements ParameterValidator<CommandActor, Number>
{
    INSTANCE;


    @Override
    public void validate(@NotNull CommandActor actor, Number value, @NotNull ParameterNode<CommandActor, Number> parameter, @NotNull Lamp<CommandActor> lamp) {
        Range range = parameter.annotations().get(Range.class);
        if (range == null) {
            return;
        }
        if (value == null) {
            return;
        }
        if (value.doubleValue() > range.max() || value.doubleValue() < range.min()) {
            throw new NumberNotInRangeException(value, range.min(), range.max());
        }
    }
}

