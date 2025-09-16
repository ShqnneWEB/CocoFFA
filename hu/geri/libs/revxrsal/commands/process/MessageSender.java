/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.process;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface MessageSender<A extends CommandActor, T> {
    public void send(@NotNull A var1, @NotNull T var2);
}

