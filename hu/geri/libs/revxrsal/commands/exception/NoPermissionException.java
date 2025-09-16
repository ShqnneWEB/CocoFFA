/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.ThrowableFromCommand;
import hu.geri.libs.revxrsal.commands.node.RequiresPermission;
import org.jetbrains.annotations.NotNull;

@ThrowableFromCommand
public class NoPermissionException
extends RuntimeException {
    @NotNull
    private final RequiresPermission<?> target;

    public NoPermissionException(@NotNull RequiresPermission<?> target) {
        this.target = target;
    }

    @NotNull
    public <A extends CommandActor> RequiresPermission<A> target() {
        return this.target;
    }
}

