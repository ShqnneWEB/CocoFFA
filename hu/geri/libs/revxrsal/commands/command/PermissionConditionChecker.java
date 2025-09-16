/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.command;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.NoPermissionException;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.process.CommandCondition;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public enum PermissionConditionChecker implements CommandCondition<CommandActor>
{
    INSTANCE;


    @Override
    public void test(@NotNull ExecutionContext<CommandActor> context) {
        if (!context.command().permission().isExecutableBy(context.actor())) {
            throw new NoPermissionException(context.command());
        }
    }
}

