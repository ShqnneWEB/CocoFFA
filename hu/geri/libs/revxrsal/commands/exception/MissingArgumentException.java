/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.exception.ThrowableFromCommand;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;

@ThrowableFromCommand
public class MissingArgumentException
extends RuntimeException {
    private final ParameterNode<CommandActor, Object> node;
    private final ExecutableCommand<CommandActor> command;

    public <A extends CommandActor> MissingArgumentException(ParameterNode<A, ?> node, ExecutableCommand<A> command) {
        this.node = node;
        this.command = command;
    }

    public <A extends CommandActor> ParameterNode<A, Object> node() {
        return this.node;
    }

    public ExecutableCommand<CommandActor> command() {
        return this.command;
    }
}

