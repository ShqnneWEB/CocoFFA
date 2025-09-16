/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node.parser;

import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandParameter;
import hu.geri.libs.revxrsal.commands.command.CommandPermission;
import hu.geri.libs.revxrsal.commands.node.CommandNode;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.node.parser.MutableCommandNode;
import hu.geri.libs.revxrsal.commands.node.parser.ParameterNodeImpl;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import org.jetbrains.annotations.NotNull;

final class MutableParameterNode<A extends CommandActor, T>
extends MutableCommandNode<A>
implements Comparable<MutableParameterNode<A, Object>> {
    @NotNull
    private ParameterType<A, T> type;
    @NotNull
    private SuggestionProvider<A> suggestions = SuggestionProvider.empty();
    @NotNull
    private CommandPermission<A> permission = CommandPermission.alwaysTrue();
    @NotNull
    private CommandParameter parameter;
    private boolean isOptional;

    public MutableParameterNode(@NotNull String name) {
        super(name);
    }

    @Override
    public int compareTo(@NotNull MutableParameterNode<A, Object> o) {
        return this.type.parsePriority().comparator().compare(this.type(), o.type());
    }

    @NotNull
    public ParameterNode<A, T> createNode() {
        return new ParameterNodeImpl(this.getName(), this.getAction(), this.isLast(), this.type, this.suggestions, this.parameter, this.permission, this.isOptional);
    }

    public void setType(@NotNull ParameterType<A, T> type) {
        this.type = type;
        if (this.suggestions == SuggestionProvider.empty()) {
            this.suggestions = type.defaultSuggestions();
        }
    }

    @Override
    public CommandNode<A> toNode() {
        return this.createNode();
    }

    @NotNull
    public ParameterType<A, T> type() {
        return this.type;
    }

    @NotNull
    public SuggestionProvider<A> suggestions() {
        return this.suggestions;
    }

    @NotNull
    public CommandParameter parameter() {
        return this.parameter;
    }

    public boolean isOptional() {
        return this.isOptional;
    }

    public void setSuggestions(@NotNull SuggestionProvider<A> suggestions) {
        if (suggestions == null) {
            throw new NullPointerException("suggestions is marked non-null but is null");
        }
        this.suggestions = suggestions;
    }

    public void setPermission(@NotNull CommandPermission<A> permission) {
        if (permission == null) {
            throw new NullPointerException("permission is marked non-null but is null");
        }
        this.permission = permission;
    }

    public void setParameter(@NotNull CommandParameter parameter) {
        if (parameter == null) {
            throw new NullPointerException("parameter is marked non-null but is null");
        }
        this.parameter = parameter;
    }

    public void setOptional(boolean isOptional) {
        this.isOptional = isOptional;
    }
}

