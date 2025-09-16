/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node.parser;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.Flag;
import hu.geri.libs.revxrsal.commands.annotation.NotSender;
import hu.geri.libs.revxrsal.commands.annotation.Single;
import hu.geri.libs.revxrsal.commands.annotation.Switch;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandFunction;
import hu.geri.libs.revxrsal.commands.command.CommandParameter;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.node.parser.BaseCommandNode;
import hu.geri.libs.revxrsal.commands.node.parser.Execution;
import hu.geri.libs.revxrsal.commands.node.parser.MutableCommandNode;
import hu.geri.libs.revxrsal.commands.node.parser.MutableLiteralNode;
import hu.geri.libs.revxrsal.commands.node.parser.MutableParameterNode;
import hu.geri.libs.revxrsal.commands.node.parser.ReflectionAction;
import hu.geri.libs.revxrsal.commands.parameter.ParameterResolver;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.parameter.StringParameterType;
import hu.geri.libs.revxrsal.commands.parameter.builtins.SenderContextParameter;
import hu.geri.libs.revxrsal.commands.process.SenderResolver;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import hu.geri.libs.revxrsal.commands.stream.token.LiteralToken;
import hu.geri.libs.revxrsal.commands.stream.token.ParameterToken;
import hu.geri.libs.revxrsal.commands.stream.token.Token;
import hu.geri.libs.revxrsal.commands.stream.token.TokenParser;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public final class TreeParser<A extends CommandActor> {
    private final LinkedList<MutableCommandNode<A>> nodes = new LinkedList();
    @NotNull
    private final CommandFunction fn;
    private final Lamp<A> lamp;
    private final Map<String, CommandParameter> methodParameters;
    private final Set<String> usedLongNames = new HashSet<String>();
    private final Set<Character> usedShortNames = new HashSet<Character>();
    private boolean requireOptionals;
    private boolean requireFlags;

    private TreeParser(@NotNull CommandFunction fn, @NotNull Lamp<A> lamp) {
        this.fn = fn;
        this.methodParameters = new LinkedHashMap<String, CommandParameter>(fn.parametersByName());
        this.lamp = lamp;
    }

    @NotNull
    public static <A extends CommandActor> ExecutableCommand<A> parse(@NotNull CommandFunction function, @NotNull Lamp<A> lamp, @NotNull MutableStringStream input) {
        TreeParser<A> parser = new TreeParser<A>(function, lamp);
        return super.parse(input);
    }

    private Execution<A> parse(@NotNull MutableStringStream input) {
        ReflectionAction<A> action = new ReflectionAction<A>(this.fn);
        this.checkNotEmpty(input);
        while (input.hasRemaining()) {
            Token firstToken;
            Iterator node2;
            if (input.peek() == ' ') {
                input.moveForward();
            }
            if (this.isOptional((MutableCommandNode<A>)((Object)(node2 = this.generateNode(firstToken = TokenParser.parseNextToken(input)))))) {
                ((MutableCommandNode)((Object)node2)).setAction(action);
            }
            this.checkOrder((MutableCommandNode<A>)((Object)node2), input);
            this.pushNode((MutableCommandNode<A>)((Object)node2));
        }
        if (!this.methodParameters.isEmpty()) {
            ArrayList<CommandParameter> arguments = new ArrayList<CommandParameter>();
            for (CommandParameter commandParameter : this.methodParameters.values()) {
                if (this.addSenderIfFirst(commandParameter, action)) continue;
                ParameterResolver parameterResolver = this.lamp.resolver(commandParameter);
                if (parameterResolver.isParameterType()) {
                    arguments.add(commandParameter);
                    continue;
                }
                if (!parameterResolver.isContextParameter()) continue;
                action.addContextParameter(commandParameter, parameterResolver.requireContextParameter());
            }
            for (CommandParameter commandParameter : arguments) {
                MutableParameterNode mutableParameterNode = this.createParameterNode(commandParameter);
                if (this.isOptional(mutableParameterNode)) {
                    mutableParameterNode.setAction(action);
                }
                this.checkOrder(mutableParameterNode, input);
                this.pushNode(mutableParameterNode);
            }
        }
        MutableCommandNode last = this.nodes.getLast();
        last.setLast(true);
        last.setAction(action);
        if (this.isParameter(last)) {
            this.setIfGreedy(this.p(last));
        }
        ArrayList executionNodes = new ArrayList();
        for (MutableCommandNode mutableCommandNode : this.nodes) {
            if (this.isParameter(mutableCommandNode) && this.p(mutableCommandNode).type().isGreedy() && !mutableCommandNode.isLast()) {
                throw new IllegalArgumentException("Found a greedy parameter (" + mutableCommandNode.getName() + ") in the middle of the command. Greedy parameters can only come at the end of the command.");
            }
            executionNodes.add(mutableCommandNode.toNode());
        }
        Execution execution = new Execution(this.fn, executionNodes);
        execution.forEach(node -> {
            ((BaseCommandNode)node).setCommand(executableCommand);
            ((BaseCommandNode)node).setLamp(this.lamp);
        });
        return execution;
    }

    private boolean addSenderIfFirst(CommandParameter param, ReflectionAction<A> action) {
        if (param.methodIndex() == 0 && !param.hasAnnotation(NotSender.class) && !param.annotations().any(a -> a.annotationType().isAnnotationPresent(NotSender.ImpliesNotSender.class))) {
            for (SenderResolver<A> senderResolver : this.lamp.senderResolvers()) {
                if (!senderResolver.isSenderType(param)) continue;
                SenderContextParameter resolver = new SenderContextParameter(senderResolver);
                action.addContextParameter(param, resolver);
                return true;
            }
        }
        return false;
    }

    private boolean isGreedy(MutableParameterNode<A, Object> argument) {
        return argument.isLast() && !argument.parameter().annotations().contains(Single.class);
    }

    private void checkOrder(MutableCommandNode<A> node, @NotNull MutableStringStream src) {
        if (this.requireOptionals) {
            if (this.isLiteral(node)) {
                throw new IllegalArgumentException("Found a literal path (" + node.getName() + ") sitting between optional parameters (full path: " + src.source() + "). Optional parameters must all come successively at the end of the command");
            }
            if (this.isParameter(node) && !this.isOptional(node)) {
                throw new IllegalArgumentException("Found a non-optional parameter (" + node.getName() + ") sitting between optional parameters (full path: " + src.source() + "). Optional parameters must all come successively at the end of the command");
            }
        }
        if (this.requireFlags) {
            if (this.isLiteral(node)) {
                throw new IllegalArgumentException("Found a literal path (" + node.getName() + ") sitting between flag/switch parameters (full path: " + src.source() + "). Flags and switches must all come successively at the end of the command");
            }
            if (this.isParameter(node) && !this.isFlagOrSwitch(node)) {
                throw new IllegalArgumentException("Found a non-flag parameter (" + node.getName() + ") sitting between flags/switches parameters (full path: " + src.source() + "). Flags and switches must all come successively at the end of the command");
            }
        }
    }

    private void checkNotEmpty(StringStream input) {
        if (input.hasFinished()) {
            if (input.source().isEmpty()) {
                throw new IllegalStateException("The input is empty");
            }
            throw new IllegalStateException("The input has already been consumed. Called parse() twice?");
        }
    }

    private void pushNode(MutableCommandNode<A> node) {
        if (this.nodes.isEmpty() && !this.isLiteral(node)) {
            throw new IllegalArgumentException("First node must be a literal.");
        }
        this.validateFlagName(node);
        this.nodes.addLast(node);
    }

    private void validateFlagName(MutableCommandNode<A> node) {
        if (!(node instanceof MutableParameterNode)) {
            return;
        }
        MutableParameterNode parameter = (MutableParameterNode)node;
        Switch switchAnn = parameter.parameter().getAnnotation(Switch.class);
        Flag flag = parameter.parameter().getAnnotation(Flag.class);
        if (flag != null) {
            this.validate(node, flag.value(), flag.shorthand());
        }
        if (switchAnn != null) {
            this.validate(node, switchAnn.value(), switchAnn.shorthand());
        }
    }

    private void validate(MutableCommandNode<A> node, String value, char shorthand) {
        String name = value.isEmpty() ? node.getName() : value;
        Character shortcut = Character.valueOf(shorthand == '\u0000' ? name.charAt(0) : shorthand);
        if (!this.usedLongNames.add(name)) {
            throw new IllegalArgumentException("Duplicate flag name: " + value);
        }
        if (!this.usedShortNames.add(shortcut)) {
            throw new IllegalArgumentException("Duplicate flag shorthand name: " + shortcut);
        }
    }

    private MutableCommandNode<A> generateNode(@NotNull Token token) {
        if (token instanceof LiteralToken) {
            return this.createLiteralNode((LiteralToken)token);
        }
        if (token instanceof ParameterToken) {
            return this.createParameterNode((ParameterToken)token);
        }
        throw new IllegalArgumentException("Don't know how to deal with token: " + token);
    }

    @NotNull
    private MutableLiteralNode<A> createLiteralNode(@NotNull LiteralToken token) {
        return new MutableLiteralNode(token.value());
    }

    private boolean isParameter(@NotNull MutableCommandNode<A> node) {
        return node instanceof MutableParameterNode;
    }

    private boolean isLiteral(@NotNull MutableCommandNode<A> node) {
        return node instanceof MutableLiteralNode;
    }

    private boolean isOptional(MutableCommandNode<A> node) {
        return node instanceof MutableParameterNode && ((MutableParameterNode)node).isOptional();
    }

    private boolean isFlagOrSwitch(MutableCommandNode<A> node) {
        return node instanceof MutableParameterNode && (((MutableParameterNode)node).parameter().hasAnnotation(Switch.class) || ((MutableParameterNode)node).parameter().hasAnnotation(Flag.class));
    }

    private MutableParameterNode<A, Object> p(@NotNull MutableCommandNode<A> node) {
        return (MutableParameterNode)node;
    }

    @NotNull
    private CommandParameter popParameter(@NotNull String name) {
        CommandParameter parameter = this.methodParameters.remove(name);
        if (parameter == null) {
            throw new IllegalArgumentException("Couldn't find a parameter in method " + this.fn.method() + " named '" + name + "'. Available names: " + this.methodParameters.keySet() + ".");
        }
        return parameter;
    }

    private MutableParameterNode<A, Object> createParameterNode(ParameterToken token) {
        CommandParameter parameter = this.popParameter(token.name());
        return this.createParameterNode(parameter);
    }

    private MutableParameterNode<A, Object> createParameterNode(CommandParameter parameter) {
        ParameterType parameterType = this.lamp.resolver(parameter).requireParameterType(parameter.fullType());
        MutableParameterNode argument = new MutableParameterNode(parameter.name());
        argument.setType(parameterType);
        argument.setParameter(parameter);
        if (parameter.isOptional()) {
            argument.setOptional(true);
            this.requireOptionals = true;
        }
        if (parameter.hasAnnotation(Flag.class) || parameter.hasAnnotation(Switch.class)) {
            this.requireFlags = true;
        }
        argument.setPermission(this.lamp.createPermission(parameter.annotations()));
        this.setSuggestions(argument);
        return argument;
    }

    private void setSuggestions(MutableParameterNode<A, Object> argument) {
        SuggestionProvider<A> provider = this.lamp.suggestionProvider(argument.parameter());
        if (provider != SuggestionProvider.empty()) {
            argument.setSuggestions(provider);
        }
    }

    private void setIfGreedy(@NotNull MutableParameterNode<A, Object> argument) {
        if (this.isGreedy(argument) && argument.type().equals(StringParameterType.single()) && !argument.parameter().hasAnnotation(Flag.class)) {
            argument.setType(StringParameterType.greedy());
        }
    }
}

