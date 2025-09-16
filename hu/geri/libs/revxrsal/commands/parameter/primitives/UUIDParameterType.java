/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter.primitives;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.InvalidUUIDException;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class UUIDParameterType
implements ParameterType<CommandActor, UUID> {
    @Override
    public UUID parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<CommandActor> context) {
        String value = input.readUnquotedString();
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidUUIDException(value);
        }
    }
}

