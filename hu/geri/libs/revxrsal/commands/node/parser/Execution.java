/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node.parser;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.CommandPriority;
import hu.geri.libs.revxrsal.commands.annotation.Description;
import hu.geri.libs.revxrsal.commands.annotation.SecretCommand;
import hu.geri.libs.revxrsal.commands.annotation.Usage;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandFunction;
import hu.geri.libs.revxrsal.commands.command.CommandPermission;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.command.Potential;
import hu.geri.libs.revxrsal.commands.exception.ExpectedLiteralException;
import hu.geri.libs.revxrsal.commands.exception.InputParseException;
import hu.geri.libs.revxrsal.commands.exception.context.ErrorContext;
import hu.geri.libs.revxrsal.commands.help.Help;
import hu.geri.libs.revxrsal.commands.node.CommandNode;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.node.MutableExecutionContext;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.node.parser.FlagParser;
import hu.geri.libs.revxrsal.commands.node.parser.HelpImpl;
import hu.geri.libs.revxrsal.commands.node.parser.LiteralNodeImpl;
import hu.geri.libs.revxrsal.commands.node.parser.ParameterNodeImpl;
import hu.geri.libs.revxrsal.commands.process.CommandCondition;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.StringJoiner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

final class Execution<A extends CommandActor>
implements ExecutableCommand<A> {
    private final CommandFunction function;
    private final List<CommandNode<A>> nodes;
    private final @Unmodifiable Map<String, ParameterNode<A, Object>> parameters;
    private final CommandPermission<A> permission;
    private final int size;
    private final boolean isSecret;
    private final String description;
    private final String usage;
    private final OptionalInt priority;
    private final String siblingPath;
    private final String path;
    private final int flagCount;
    private final boolean lowPriority;
    private int optionalParameters;
    private int requiredInput;

    public Execution(CommandFunction function, List<CommandNode<A>> nodes) {
        this.function = function;
        this.nodes = nodes;
        this.parameters = this.computeParameters();
        this.size = nodes.size();
        this.permission = function.lamp().createPermission(function.annotations());
        for (CommandNode<A> node : nodes) {
            if (Execution.isOptional(node)) {
                ++this.optionalParameters;
                continue;
            }
            ++this.requiredInput;
        }
        this.isSecret = function.annotations().contains(SecretCommand.class);
        this.description = function.annotations().map(Description.class, Description::value);
        this.path = this.computePath();
        this.usage = function.annotations().mapOrGet(Usage.class, Usage::value, this::path);
        this.priority = function.annotations().mapOr(CommandPriority.class, c -> OptionalInt.of(c.value()), OptionalInt.empty());
        this.siblingPath = this.computeSiblingPath();
        this.flagCount = hu.geri.libs.revxrsal.commands.util.Collections.count(nodes, n -> n instanceof ParameterNode && (((ParameterNode)n).isFlag() || ((ParameterNode)n).isSwitch()));
        this.lowPriority = function.annotations().contains(CommandPriority.Low.class);
        if (this.lowPriority && this.priority.isPresent()) {
            throw new IllegalArgumentException("You cannot have @CommandPriority and @CommandPriority.Low on the same function!");
        }
    }

    private static boolean isOptional(@NotNull CommandNode<? extends CommandActor> node) {
        return node instanceof ParameterNodeImpl && ((ParameterNode)node).isOptional();
    }

    private @Unmodifiable Map<String, ParameterNode<A, Object>> computeParameters() {
        LinkedHashMap<String, ParameterNode> parameters = new LinkedHashMap<String, ParameterNode>();
        for (CommandNode<A> node : this.nodes) {
            if (!(node instanceof ParameterNode)) continue;
            ParameterNode parameter = (ParameterNode)node;
            parameters.put(parameter.name(), parameter);
        }
        return Collections.unmodifiableMap(parameters);
    }

    private String computePath() {
        StringJoiner joiner = new StringJoiner(" ");
        for (CommandNode<A> n : this.nodes) {
            joiner.add(n.representation());
        }
        return joiner.toString();
    }

    @NotNull
    private String computeSiblingPath() {
        CommandNode<A> n;
        int i;
        StringJoiner joiner = new StringJoiner(" ");
        int index = 0;
        for (i = this.nodes.size() - 1; i >= 0; --i) {
            n = this.nodes.get(i);
            if (!n.isLiteral()) continue;
            index = i;
            break;
        }
        for (i = 0; i < index; ++i) {
            n = this.nodes.get(i);
            joiner.add(n.representation());
        }
        return joiner.toString();
    }

    @Override
    @NotNull
    public Lamp<A> lamp() {
        return this.function.lamp();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public int optionalParameters() {
        return this.optionalParameters;
    }

    @Override
    public int requiredInput() {
        return this.requiredInput;
    }

    @Override
    @NotNull
    public String path() {
        return this.path;
    }

    @Override
    @NotNull
    public String usage() {
        return this.usage;
    }

    @Override
    @NotNull
    public CommandPermission<A> permission() {
        return this.permission;
    }

    @Override
    @NotNull
    public CommandFunction function() {
        return this.function;
    }

    @Override
    @NotNull
    public CommandNode<A> lastNode() {
        return this.nodes.get(this.nodes.size() - 1);
    }

    @Override
    @NotNull
    public LiteralNodeImpl<A> firstNode() {
        return (LiteralNodeImpl)this.nodes.get(0);
    }

    @Override
    @NotNull
    public Potential<A> test(@NotNull A actor, @NotNull MutableStringStream input) {
        return new ParseResult<A>(this, actor, input);
    }

    @Override
    public void unregister() {
        this.lamp().unregister(this);
    }

    @Override
    public void execute(@NotNull ExecutionContext<A> context) {
        try {
            for (CommandCondition<A> condition : context.lamp().commandConditions()) {
                condition.test(context);
            }
            this.action().execute(context);
        } catch (Throwable t) {
            this.lamp().handleException(t, ErrorContext.executingFunction(context));
        }
    }

    @Override
    public @NotNull Help.RelatedCommands<A> relatedCommands(@Nullable A filterFor) {
        return new HelpImpl.RelatedCommandsImpl(hu.geri.libs.revxrsal.commands.util.Collections.filter(this.lamp().registry().commands(), command -> command != this && !command.isSecret() && this.isRelatedTo(command) && (filterFor == null || command.permission().isExecutableBy((CommandActor)filterFor))));
    }

    @Override
    public @NotNull Help.ChildrenCommands<A> childrenCommands(@Nullable A filterFor) {
        return new HelpImpl.ChildrenCommandsImpl(hu.geri.libs.revxrsal.commands.util.Collections.filter(this.lamp().registry().commands(), command -> command != this && !command.isSecret() && this.isParentOf(command) && (filterFor == null || command.permission().isExecutableBy((CommandActor)filterFor))));
    }

    @Override
    public @NotNull Help.SiblingCommands<A> siblingCommands(@Nullable A filterFor) {
        return new HelpImpl.SiblingCommandsImpl(hu.geri.libs.revxrsal.commands.util.Collections.filter(this.lamp().registry().commands(), command -> command != this && !command.isSecret() && this.isSiblingOf((ExecutableCommand<A>)command) && (filterFor == null || command.permission().isExecutableBy((CommandActor)filterFor))));
    }

    @Override
    @NotNull
    public @Unmodifiable Map<String, ParameterNode<A, Object>> parameters() {
        return this.parameters;
    }

    @Override
    public boolean isSiblingOf(@NotNull ExecutableCommand<A> command) {
        String otherPath = ((Execution)command).siblingPath;
        return command != this && otherPath.startsWith(this.siblingPath) || this.siblingPath.startsWith(otherPath);
    }

    @Override
    public boolean isChildOf(@NotNull ExecutableCommand<A> command) {
        if (this.size() <= command.size()) {
            return false;
        }
        for (int i = 0; i < command.size(); ++i) {
            CommandNode<A> ourNode = this.nodes.get(i);
            CommandNode<A> otherNode = command.nodes().get(i);
            if (otherNode.representation().equals(ourNode.representation())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean containsFlags() {
        return this.flagCount > 0;
    }

    @Override
    public boolean isSecret() {
        return this.isSecret;
    }

    public String toString() {
        return "ExecutableCommand(path='" + this.path() + "')";
    }

    @Override
    @Nullable
    public String description() {
        return this.description;
    }

    @Override
    public @Range(from=0L, to=0x7FFFFFFFL) int flagCount() {
        return this.flagCount;
    }

    @Override
    @NotNull
    public @Unmodifiable List<CommandNode<A>> nodes() {
        return Collections.unmodifiableList(this.nodes);
    }

    @Override
    @NotNull
    public OptionalInt commandPriority() {
        return this.priority;
    }

    @Override
    public int compareTo(@NotNull ExecutableCommand<A> o) {
        if (!(o instanceof Execution)) {
            return 0;
        }
        Execution exec = (Execution)o;
        if (this.lowPriority != exec.lowPriority) {
            return this.lowPriority ? 1 : -1;
        }
        if (this.commandPriority().isPresent() && o.commandPriority().isPresent()) {
            return Integer.compare(this.commandPriority().getAsInt(), o.commandPriority().getAsInt());
        }
        int sizeComparison = Integer.compare(this.size, exec.size);
        if (sizeComparison != 0) {
            return sizeComparison;
        }
        if (Execution.isOptional(this.lastNode()) != Execution.isOptional(o.lastNode())) {
            return Execution.isOptional(this.lastNode()) ? 1 : -1;
        }
        return this.lastNode().compareTo(o.lastNode());
    }

    @Override
    @NotNull
    public Iterator<CommandNode<A>> iterator() {
        return hu.geri.libs.revxrsal.commands.util.Collections.unmodifiableIterator(this.nodes.iterator());
    }

    static final class ParseResult<A extends CommandActor>
    implements Potential<A> {
        private final Execution<A> execution;
        private final MutableExecutionContext<A> context;
        private final boolean testResult;
        private MutableStringStream input;
        private boolean consumedAllInput = false;
        @Nullable
        private Throwable error;
        @Nullable
        private ErrorContext<A> errorContext;

        public ParseResult(Execution<A> execution, A actor, MutableStringStream input) {
            this.execution = execution;
            this.context = ExecutionContext.createMutable(execution, actor, input.toImmutableCopy());
            this.input = input;
            this.testResult = this.test();
        }

        private boolean test() {
            if (((Execution)this.execution).flagCount > 0) {
                MutableStringStream original = this.input.toMutableCopy();
                if (!this.tryParseFlags()) {
                    this.input = original;
                    return false;
                }
            }
            for (CommandNode node : ((Execution)this.execution).nodes) {
                if (node instanceof ParameterNode && (((ParameterNode)node).isFlag() || ((ParameterNode)node).isSwitch())) {
                    ParameterNode p = (ParameterNode)node;
                    continue;
                }
                if (this.tryParse(node, this.input, this.context)) continue;
                this.context.clearResolvedArguments();
                return false;
            }
            if (!this.testConditions()) {
                return false;
            }
            this.consumedAllInput = this.input.hasFinished();
            return true;
        }

        private boolean tryParseFlags() {
            FlagParser<A> flagParser = new FlagParser<A>(this.context, this.input);
            if (!flagParser.tryParse()) {
                this.error = flagParser.error();
                this.errorContext = flagParser.errorContext();
                this.context.clearResolvedArguments();
                return false;
            }
            this.input = flagParser.strippedInput();
            return true;
        }

        private boolean testConditions() {
            try {
                for (CommandCondition<A> condition : this.context.lamp().commandConditions()) {
                    condition.test(this.context);
                }
                return true;
            } catch (Throwable t) {
                this.error = t;
                this.errorContext = ErrorContext.executingFunction(this.context);
                return false;
            }
        }

        @Override
        public boolean successful() {
            return this.testResult;
        }

        @Override
        @NotNull
        public ExecutionContext<A> context() {
            return this.context;
        }

        @Override
        public boolean failed() {
            return !this.testResult;
        }

        @Override
        public void handleException() {
            if (this.error != null && this.errorContext != null) {
                this.context().lamp().handleException(this.error, this.errorContext);
            }
        }

        @Override
        @Nullable
        public Throwable error() {
            return this.error;
        }

        @Override
        @Nullable
        public ErrorContext<A> errorContext() {
            return this.errorContext;
        }

        @Override
        public void execute() {
            if (this.error == null && this.execution.lamp().hooks().onCommandExecuted(this.execution, this.context)) {
                this.execution.lastNode().execute(this.context, this.input);
            }
        }

        @Override
        public int compareTo(@NotNull Potential<A> o) {
            if (o.getClass() != this.getClass()) {
                return 0;
            }
            ParseResult result = (ParseResult)o;
            if (this.consumedAllInput != result.consumedAllInput) {
                return this.consumedAllInput ? -1 : 1;
            }
            return this.execution.compareTo(result.execution);
        }

        private boolean tryParse(CommandNode<A> node, MutableStringStream input, MutableExecutionContext<A> context) {
            if (input.hasRemaining() && input.peek() == ' ') {
                input.skipWhitespace();
            }
            int pos = input.position();
            if (node instanceof LiteralNodeImpl) {
                LiteralNodeImpl l = (LiteralNodeImpl)node;
                String value = input.readUnquotedString();
                if (node.name().equalsIgnoreCase(value)) {
                    this.checkForSpace(input);
                    return true;
                }
                input.setPosition(pos);
                this.error = new ExpectedLiteralException(value, l);
                this.errorContext = ErrorContext.parsingLiteral(context, l);
                return false;
            }
            ParameterNodeImpl parameter = (ParameterNodeImpl)node;
            try {
                Object value = parameter.parse(input, context);
                Lamp lamp = this.execution.function().lamp();
                context.addResolvedArgument(parameter.name(), value);
                this.checkForSpace(input);
                return true;
            } catch (Throwable t) {
                input.setPosition(pos);
                this.error = t;
                this.errorContext = ErrorContext.parsingParameter(context, parameter, input);
                return false;
            }
        }

        private void checkForSpace(MutableStringStream input) {
            if (input.hasRemaining() && input.peek() != ' ') {
                throw new InputParseException(InputParseException.Cause.EXPECTED_WHITESPACE);
            }
        }

        public String toString() {
            if (this.successful()) {
                return "Potential(path=" + this.execution.path() + ", success=true)";
            }
            return "Potential(path=" + this.execution.path() + ", success=false, error=" + this.error + ")";
        }
    }
}

