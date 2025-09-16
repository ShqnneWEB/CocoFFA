/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.CheckReturnValue
 */
package hu.geri.libs.revxrsal.commands.command;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandFunction;
import hu.geri.libs.revxrsal.commands.command.Potential;
import hu.geri.libs.revxrsal.commands.help.Help;
import hu.geri.libs.revxrsal.commands.node.CommandAction;
import hu.geri.libs.revxrsal.commands.node.CommandNode;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.node.HasDescription;
import hu.geri.libs.revxrsal.commands.node.LiteralNode;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.node.RequiresPermission;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

public interface ExecutableCommand<A extends CommandActor>
extends Comparable<ExecutableCommand<A>>,
Iterable<CommandNode<A>>,
RequiresPermission<A>,
HasDescription {
    @NotNull
    public Lamp<A> lamp();

    public @Range(from=1L, to=0x7FFFFFFFL) int size();

    public int optionalParameters();

    public int requiredInput();

    @NotNull
    public String path();

    @NotNull
    public String usage();

    @Override
    @Nullable
    public String description();

    @NotNull
    public CommandFunction function();

    @NotNull
    public CommandNode<A> lastNode();

    @NotNull
    public LiteralNode<A> firstNode();

    @NotNull
    @CheckReturnValue
    public Potential<A> test(@NotNull A var1, @NotNull MutableStringStream var2);

    @NotNull
    public @Unmodifiable List<CommandNode<A>> nodes();

    @NotNull
    public OptionalInt commandPriority();

    public boolean isSecret();

    public void unregister();

    default public void execute(@NotNull A actor, @NotNull MutableStringStream input) {
        this.lamp().registry().execute(actor, this, input);
    }

    public void execute(@NotNull ExecutionContext<A> var1);

    @NotNull
    default public CommandAction<A> action() {
        return Objects.requireNonNull(this.lastNode().action(), "lastNode().action() is null");
    }

    @NotNull
    default public AnnotationList annotations() {
        return this.function().annotations();
    }

    @NotNull
    public Help.RelatedCommands<A> relatedCommands(@Nullable A var1);

    @NotNull
    default public Help.RelatedCommands<A> relatedCommands() {
        return this.relatedCommands(null);
    }

    @NotNull
    public Help.ChildrenCommands<A> childrenCommands(@Nullable A var1);

    @NotNull
    default public Help.ChildrenCommands<A> childrenCommands() {
        return this.childrenCommands(null);
    }

    @NotNull
    public Help.SiblingCommands<A> siblingCommands(@Nullable A var1);

    @NotNull
    default public Help.SiblingCommands<A> siblingCommands() {
        return this.siblingCommands(null);
    }

    @NotNull
    @Contract(pure=true)
    public @Unmodifiable Map<String, ParameterNode<A, Object>> parameters();

    @Nullable
    default public <T> ParameterNode<A, T> parameterOrNull(@NotNull String name) {
        Preconditions.notNull(name, "parameter name");
        return this.parameters().get(name);
    }

    @NotNull
    default public <T> ParameterNode<A, T> parameter(@NotNull String name) {
        ParameterNode<A, T> parameter = this.parameterOrNull(name);
        if (parameter == null) {
            throw new IllegalArgumentException("No such parameter: " + name);
        }
        return parameter;
    }

    public boolean isSiblingOf(@NotNull ExecutableCommand<A> var1);

    public boolean isChildOf(@NotNull ExecutableCommand<A> var1);

    default public boolean isParentOf(@NotNull ExecutableCommand<A> command) {
        return command.isChildOf(this);
    }

    default public boolean isRelatedTo(@NotNull ExecutableCommand<A> command) {
        return this.isParentOf(command) || this.isSiblingOf(command);
    }

    default public boolean isVisibleTo(@NotNull A actor) {
        return !this.isSecret() && this.permission().isExecutableBy(actor);
    }

    public boolean containsFlags();

    public @Range(from=0L, to=0x7FFFFFFFL) int flagCount();
}

