/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node.parser;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.CommandAction;
import hu.geri.libs.revxrsal.commands.node.CommandNode;
import hu.geri.libs.revxrsal.commands.node.LiteralNode;
import hu.geri.libs.revxrsal.commands.node.parser.BaseCommandNode;
import hu.geri.libs.revxrsal.commands.node.parser.ParameterNodeImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class LiteralNodeImpl<A extends CommandActor>
extends BaseCommandNode<A>
implements LiteralNode<A> {
    public LiteralNodeImpl(@NotNull String name, @Nullable CommandAction<A> action, boolean isLast) {
        super(name, action, isLast);
    }

    public String toString() {
        return "LiteralNode(name='" + this.name() + "')";
    }

    @Override
    public int compareTo(@NotNull CommandNode<A> o) {
        if (o instanceof ParameterNodeImpl) {
            return -1;
        }
        return 0;
    }
}

