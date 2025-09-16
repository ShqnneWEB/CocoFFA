/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.InvalidValueException;
import hu.geri.libs.revxrsal.commands.node.LiteralNode;
import org.jetbrains.annotations.NotNull;

public class ExpectedLiteralException
extends InvalidValueException {
    @NotNull
    private final LiteralNode<CommandActor> node;

    public ExpectedLiteralException(@NotNull String input, @NotNull LiteralNode<CommandActor> node) {
        super(input);
        this.node = node;
    }

    @NotNull
    public <A extends CommandActor> LiteralNode<A> node() {
        return this.node;
    }
}

