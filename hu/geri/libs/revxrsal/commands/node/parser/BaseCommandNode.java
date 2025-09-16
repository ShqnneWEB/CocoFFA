/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node.parser;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.node.CommandAction;
import hu.geri.libs.revxrsal.commands.node.CommandNode;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class BaseCommandNode<A extends CommandActor>
implements CommandNode<A> {
    @NotNull
    private final String name;
    @Nullable
    private final CommandAction<A> action;
    private final boolean isLast;
    private Lamp<A> lamp;
    private ExecutableCommand<A> command;

    @Override
    public void execute(@NotNull ExecutionContext<A> context, @NotNull MutableStringStream input) {
        if (this.action != null) {
            this.action.execute(context);
        }
    }

    @Override
    @NotNull
    public String name() {
        return this.name;
    }

    @Override
    @Nullable
    public CommandAction<A> action() {
        return this.action;
    }

    @Override
    public boolean isLast() {
        return this.isLast;
    }

    @Override
    @NotNull
    public Lamp<A> lamp() {
        return this.lamp;
    }

    @Override
    @NotNull
    public ExecutableCommand<A> command() {
        return this.command;
    }

    public BaseCommandNode(@NotNull String name, @Nullable CommandAction<A> action, boolean isLast) {
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        }
        this.name = name;
        this.action = action;
        this.isLast = isLast;
    }

    public void setLamp(Lamp<A> lamp) {
        this.lamp = lamp;
    }

    public void setCommand(ExecutableCommand<A> command) {
        this.command = command;
    }
}

