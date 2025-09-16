/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandPermission;
import org.jetbrains.annotations.NotNull;

public interface RequiresPermission<A extends CommandActor> {
    @NotNull
    public CommandPermission<A> permission();
}

