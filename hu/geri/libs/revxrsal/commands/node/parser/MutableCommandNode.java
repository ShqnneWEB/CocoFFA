/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node.parser;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.CommandAction;
import hu.geri.libs.revxrsal.commands.node.CommandNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class MutableCommandNode<A extends CommandActor> {
    @NotNull
    private final String name;
    @Nullable
    private CommandAction<A> action = null;
    private boolean last;

    public abstract CommandNode<A> toNode();

    @NotNull
    public String getName() {
        return this.name;
    }

    @Nullable
    public CommandAction<A> getAction() {
        return this.action;
    }

    public boolean isLast() {
        return this.last;
    }

    public void setAction(@Nullable CommandAction<A> action) {
        this.action = action;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public String toString() {
        return "MutableCommandNode(name=" + this.getName() + ", action=" + this.getAction() + ", last=" + this.isLast() + ")";
    }

    public MutableCommandNode(@NotNull String name) {
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        }
        this.name = name;
    }
}

