/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.node.CommandAction;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.node.LiteralNode;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CommandNode<A extends CommandActor>
extends Comparable<CommandNode<A>> {
    @NotNull
    public Lamp<A> lamp();

    @NotNull
    public ExecutableCommand<A> command();

    @NotNull
    public String name();

    @Nullable
    @Contract(pure=true)
    public CommandAction<A> action();

    public boolean isLast();

    default public boolean hasAction() {
        return this.action() != null;
    }

    public void execute(@NotNull ExecutionContext<A> var1, @NotNull MutableStringStream var2);

    public boolean isLiteral();

    @NotNull
    public LiteralNode<A> requireLiteralNode();

    public boolean isParameter();

    @NotNull
    public <T> ParameterNode<A, T> requireParameterNode();

    @NotNull
    public String representation();
}

