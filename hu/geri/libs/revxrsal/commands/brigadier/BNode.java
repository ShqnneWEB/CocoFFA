/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 */
package hu.geri.libs.revxrsal.commands.brigadier;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import hu.geri.libs.revxrsal.commands.brigadier.BrigadierParser;
import hu.geri.libs.revxrsal.commands.brigadier.Nodes;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.util.function.Predicate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

final class BNode<S> {
    private final CommandNode<S> node;

    private BNode(CommandNode<S> node) {
        this.node = node;
    }

    @NotNull
    public static <S> BNode<S> of(@NotNull CommandNode<S> node) {
        Preconditions.notNull(node, "node");
        return new BNode<S>(node);
    }

    @NotNull
    public static <S> BNode<S> literal(@NotNull String name) {
        return BNode.of(LiteralArgumentBuilder.literal((String)name).build());
    }

    @NotNull
    public BNode<S> executes(Command<S> command) {
        Nodes.setCommand(this.node, command);
        return this;
    }

    @NotNull
    public BNode<S> requires(Predicate<S> requirement) {
        Nodes.setRequirement(this.node, requirement);
        return this;
    }

    @NotNull
    public BNode<S> suggests(SuggestionProvider<S> suggestionProvider) {
        if (this.node instanceof ArgumentCommandNode) {
            ArgumentCommandNode argument = (ArgumentCommandNode)this.node;
            Nodes.setSuggestionProvider(argument, suggestionProvider);
        }
        return this;
    }

    @NotNull
    public BNode<S> then(@NotNull BNode<S> node) {
        BrigadierParser.addChild(this.node, node.node);
        return this;
    }

    @NotNull
    public BNode<S> then(@NotNull CommandNode<S> node) {
        BrigadierParser.addChild(this.node, node);
        return this;
    }

    @Contract(pure=true)
    @NotNull
    public CommandNode<S> asBrigadierNode() {
        return this.node;
    }

    @NotNull
    BNode<S> nextChild() {
        return BNode.of((CommandNode)this.node.getChildren().iterator().next());
    }

    public String toString() {
        return "BNode(" + this.node + ")";
    }
}

