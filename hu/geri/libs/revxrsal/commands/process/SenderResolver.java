/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.process;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandParameter;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import org.jetbrains.annotations.NotNull;

public interface SenderResolver<A extends CommandActor> {
    public boolean isSenderType(@NotNull CommandParameter var1);

    @NotNull
    public Object getSender(@NotNull Class<?> var1, @NotNull A var2, @NotNull ExecutableCommand<A> var3);
}

