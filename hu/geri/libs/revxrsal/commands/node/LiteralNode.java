/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.CommandNode;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface LiteralNode<A extends CommandActor>
extends CommandNode<A> {
    @Override
    default public boolean isLiteral() {
        return true;
    }

    @Override
    @Contract(value="-> this", pure=true)
    @NotNull
    default public LiteralNode<A> requireLiteralNode() {
        return this;
    }

    @Override
    default public boolean isParameter() {
        return false;
    }

    @Override
    @NotNull
    default public String representation() {
        return this.name();
    }

    @Override
    @Contract(value="-> fail")
    @NotNull
    default public <T> ParameterNode<A, T> requireParameterNode() {
        throw new IllegalStateException("Expected a ParameterNode, found a LiteralNode");
    }
}

