/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 */
package hu.geri.libs.revxrsal.commands.brigadier;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.brigadier.BNode;
import hu.geri.libs.revxrsal.commands.brigadier.BrigadierAdapter;
import hu.geri.libs.revxrsal.commands.brigadier.BrigadierConverter;
import hu.geri.libs.revxrsal.commands.brigadier.Nodes;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandPermission;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import hu.geri.libs.revxrsal.commands.util.Collections;
import hu.geri.libs.revxrsal.commands.util.Permutations;
import hu.geri.libs.revxrsal.commands.util.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public final class BrigadierParser<S, A extends CommandActor> {
    @NotNull
    private final BrigadierConverter<A, S> converter;

    public BrigadierParser(@NotNull BrigadierConverter<A, S> converter) {
        this.converter = converter;
    }

    public static <S> void addChild(CommandNode<S> p, CommandNode<S> node) {
        if (node instanceof RootCommandNode) {
            throw new UnsupportedOperationException("Cannot add a RootCommandNode as a child to any other CommandNode");
        }
        CommandNode<S> child = Nodes.getChildren(p).get(node.getName());
        if (child != null) {
            if (node.getCommand() != null) {
                Nodes.setCommand(child, node.getCommand());
            }
            Nodes.setRequirement(child, child.getRequirement().or(node.getRequirement()));
            for (CommandNode grandchild : node.getChildren()) {
                BrigadierParser.addChild(child, grandchild);
            }
        } else {
            Nodes.getChildren(p).put(node.getName(), node);
            if (node instanceof LiteralCommandNode) {
                Map<String, LiteralCommandNode<S>> literals = Nodes.getLiterals(p);
                if (literals != null) {
                    literals.put(node.getName(), (LiteralCommandNode)node);
                } else {
                    Nodes.setHasLiterals(p, true);
                }
            } else if (node instanceof ArgumentCommandNode) {
                Nodes.getArguments(p).put(node.getName(), (ArgumentCommandNode)node);
            }
        }
    }

    @NotNull
    public LiteralCommandNode<S> createNode(@NotNull ExecutableCommand<A> command) {
        BNode<S> firstNode = BNode.literal(command.firstNode().name());
        firstNode.requires(this.createRequirement(command.permission(), command.lamp()));
        BNode<S> lastNode = firstNode;
        @Unmodifiable List<hu.geri.libs.revxrsal.commands.node.CommandNode<A>> nodes = command.nodes();
        for (int i = 1; i < nodes.size(); ++i) {
            BNode<S> elementNode;
            hu.geri.libs.revxrsal.commands.node.CommandNode<A> node = nodes.get(i);
            if (node.isLiteral()) {
                elementNode = BNode.literal(node.name());
                elementNode.requires(this.createRequirement(command.permission(), command.lamp()));
            } else if (node instanceof ParameterNode) {
                ParameterNode parameterNode = (ParameterNode)node;
                if (parameterNode.isSwitch() || parameterNode.isFlag()) break;
                elementNode = BNode.of(this.ofParameter(parameterNode));
                if (parameterNode.isOptional()) {
                    lastNode.executes(this.createAction(command));
                }
            } else {
                throw new UnsupportedOperationException();
            }
            lastNode.then(elementNode);
            lastNode = elementNode;
        }
        if (!command.containsFlags()) {
            lastNode.executes(this.createAction(command));
            return (LiteralCommandNode)firstNode.asBrigadierNode();
        }
        List flags = Collections.filter(command.parameters().values(), v -> v.isFlag() || v.isSwitch());
        if (command.flagCount() <= 4) {
            ArrayList<RootCommandNode> roots = new ArrayList<RootCommandNode>();
            for (List list : Permutations.generatePermutations(flags)) {
                RootCommandNode root = new RootCommandNode();
                roots.add(root);
                BNode<S> thisNode = BNode.of(root);
                for (ParameterNode parameter : list) {
                    if (parameter.isSwitch()) {
                        BNode<S> ofSwitch = this.ofSwitch(parameter);
                        thisNode.then(ofSwitch);
                        thisNode.executes(this.createAction(command));
                        thisNode = ofSwitch;
                        continue;
                    }
                    BNode<S> ofFlag = this.ofFlag(parameter);
                    thisNode.then(ofFlag);
                    if (parameter.isOptional()) {
                        thisNode.executes(this.createAction(command));
                    }
                    thisNode = ofFlag.nextChild();
                }
                thisNode.executes(this.createAction(command));
            }
            for (RootCommandNode rootCommandNode : roots) {
                for (CommandNode child : rootCommandNode.getChildren()) {
                    lastNode.then(child);
                }
            }
            return (LiteralCommandNode)firstNode.asBrigadierNode();
        }
        ArrayList<BNode<S>> addOptionalsTo = new ArrayList<BNode<S>>();
        for (ParameterNode parameterNode : flags) {
            if (parameterNode.isSwitch()) {
                BNode<S> ofSwitch = this.ofSwitch(parameterNode);
                addOptionalsTo.forEach(genNode -> {
                    genNode.then(ofSwitch);
                    genNode.executes(this.createAction(command));
                });
                lastNode.then(ofSwitch);
                lastNode.executes(this.createAction(command));
                addOptionalsTo.add(ofSwitch);
                continue;
            }
            if (!parameterNode.isFlag()) continue;
            BNode<S> ofFlag = this.ofFlag(parameterNode);
            addOptionalsTo.forEach(genNode -> genNode.then(ofFlag));
            if (parameterNode.isOptional()) {
                if (addOptionalsTo.isEmpty()) {
                    lastNode.executes(this.createAction(command));
                } else {
                    addOptionalsTo.forEach(genNode -> genNode.executes(this.createAction(command)));
                }
            }
            lastNode.then(ofFlag);
            addOptionalsTo.add(ofFlag.nextChild());
            if (!parameterNode.isRequired()) continue;
            lastNode = ofFlag.nextChild();
        }
        if (!addOptionalsTo.isEmpty()) {
            addOptionalsTo.forEach(genNode -> genNode.executes(this.createAction(command)));
        } else {
            lastNode.executes(this.createAction(command));
        }
        return (LiteralCommandNode)firstNode.asBrigadierNode();
    }

    @NotNull
    private <T> ArgumentCommandNode<S, T> ofParameter(ParameterNode<A, T> parameter) {
        RequiredArgumentBuilder builder = RequiredArgumentBuilder.argument((String)parameter.name(), this.converter.getArgumentType(parameter));
        return ((RequiredArgumentBuilder)builder.suggests(this.createSuggestionProvider(parameter)).requires(this.createRequirement(parameter.permission(), parameter.lamp()))).build();
    }

    private <T> BNode<S> ofFlag(ParameterNode<A, T> parameter) {
        ArgumentCommandNode<S, T> ofParameter = this.ofParameter(parameter);
        return BNode.literal("--" + parameter.flagName()).then(ofParameter);
    }

    @NotNull
    private BNode<S> ofSwitch(@NotNull ParameterNode<A, ?> parameter) {
        return BNode.literal("--" + parameter.switchName()).requires(this.createRequirement(parameter.permission(), parameter.lamp())).executes(this.createAction(parameter.lamp()));
    }

    @NotNull
    public Predicate<S> createRequirement(@NotNull CommandPermission<A> permission, @NotNull Lamp<A> lamp) {
        if (permission == CommandPermission.alwaysTrue()) {
            return x -> true;
        }
        return o -> {
            A actor = this.converter.createActor(o, lamp);
            return permission.isExecutableBy(actor);
        };
    }

    @NotNull
    public Command<S> createAction(@NotNull Lamp<A> lamp) {
        return a -> {
            MutableStringStream input = StringStream.createMutable(a.getInput());
            if (input.peekUnquotedString().contains(":")) {
                input = StringStream.createMutable(Strings.stripNamespace(a.getInput()));
            }
            A actor = this.converter.createActor(a.getSource(), lamp);
            lamp.dispatch(actor, input);
            return 1;
        };
    }

    @NotNull
    public Command<S> createAction(@NotNull ExecutableCommand<A> command) {
        return a -> {
            MutableStringStream input = StringStream.createMutable(a.getInput());
            if (input.peekUnquotedString().contains(":")) {
                input = StringStream.createMutable(Strings.stripNamespace(a.getInput()));
            }
            A actor = this.converter.createActor(a.getSource(), command.lamp());
            command.execute(actor, input);
            return 1;
        };
    }

    @Nullable
    public SuggestionProvider<S> createSuggestionProvider(@NotNull ParameterNode<A, ?> parameter) {
        return BrigadierAdapter.createSuggestionProvider(parameter, this.converter);
    }
}

