/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node.parser;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.CommandNode;
import hu.geri.libs.revxrsal.commands.node.LiteralNode;
import hu.geri.libs.revxrsal.commands.node.parser.LiteralNodeImpl;
import hu.geri.libs.revxrsal.commands.node.parser.MutableCommandNode;
import org.jetbrains.annotations.NotNull;

class MutableLiteralNode<A extends CommandActor>
extends MutableCommandNode<A> {
    public MutableLiteralNode(@NotNull String name) {
        super(name);
    }

    @NotNull
    public LiteralNode<A> createNode() {
        return new LiteralNodeImpl(this.getName(), this.getAction(), this.isLast());
    }

    @Override
    public CommandNode<A> toNode() {
        return this.createNode();
    }

    @Override
    public String toString() {
        return "MutableLiteralNode()";
    }
}

