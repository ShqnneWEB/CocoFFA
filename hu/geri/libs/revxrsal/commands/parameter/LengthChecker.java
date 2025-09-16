/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.Length;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.InvalidStringSizeException;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.process.ParameterValidator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public enum LengthChecker implements ParameterValidator<CommandActor, String>
{
    INSTANCE;


    @Override
    public void validate(@NotNull CommandActor actor, String value, @NotNull ParameterNode<CommandActor, String> parameter, @NotNull Lamp<CommandActor> lamp) {
        Length range = parameter.annotations().get(Length.class);
        if (range == null) {
            return;
        }
        if (value.length() > range.max() || value.length() < range.min()) {
            throw new InvalidStringSizeException(range.min(), range.max(), value);
        }
    }
}

