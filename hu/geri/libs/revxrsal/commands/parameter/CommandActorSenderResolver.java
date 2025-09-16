/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandParameter;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.process.SenderResolver;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public enum CommandActorSenderResolver implements SenderResolver<CommandActor>
{
    INSTANCE;


    @Override
    public boolean isSenderType(@NotNull CommandParameter parameter) {
        return CommandActor.class.isAssignableFrom(parameter.type());
    }

    @Override
    @NotNull
    public Object getSender(@NotNull Class<?> customSenderType, @NotNull CommandActor actor, @NotNull ExecutableCommand<CommandActor> command) {
        return actor;
    }
}

