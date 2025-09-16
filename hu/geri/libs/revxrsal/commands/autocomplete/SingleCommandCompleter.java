/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.autocomplete;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.node.CommandNode;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.node.LiteralNode;
import hu.geri.libs.revxrsal.commands.node.MutableExecutionContext;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class SingleCommandCompleter<A extends CommandActor> {
    private final ExecutableCommand<A> command;
    private final MutableStringStream input;
    private final MutableExecutionContext<A> context;
    private final List<String> suggestions = new ArrayList<String>();
    private int positionBeforeParsing = -1;

    public SingleCommandCompleter(A actor, ExecutableCommand<A> command, MutableStringStream input) {
        this.command = command;
        this.input = input;
        this.context = ExecutionContext.createMutable(command, actor, input.toImmutableView());
    }

    private void rememberPosition() {
        if (this.positionBeforeParsing != -1) {
            throw new IllegalArgumentException("You already have a position remembered that you did not consume.");
        }
        this.positionBeforeParsing = this.input.position();
    }

    private String restorePosition() {
        if (this.positionBeforeParsing == -1) {
            throw new IllegalArgumentException("You forgot to call rememberPosition() when trying to restore position.");
        }
        int positionAfterParsing = this.input.position();
        this.input.setPosition(this.positionBeforeParsing);
        this.positionBeforeParsing = -1;
        return this.input.peek(positionAfterParsing - this.positionBeforeParsing);
    }

    public void complete() {
        HashMap remainingFlags = null;
        for (CommandNode<A> node : this.command.nodes()) {
            if (node.isLiteral()) {
                CompletionResult result = this.completeLiteral(node.requireLiteralNode());
                if (result != CompletionResult.HALT) continue;
                break;
            }
            ParameterNode parameter = node.requireParameterNode();
            if (parameter.isFlag() || parameter.isSwitch()) {
                (remainingFlags == null ? new HashMap() : remainingFlags).put(this.universalFlagName(parameter), parameter);
                continue;
            }
            CompletionResult result = this.completeParameter(parameter);
            if (result != CompletionResult.HALT) continue;
            break;
        }
        if (!this.command.containsFlags() || remainingFlags == null) {
            return;
        }
        this.completeFlags(remainingFlags);
    }

    private CompletionResult completeParameter(@NotNull ParameterNode<A, Object> parameter) {
        this.rememberPosition();
        if (parameter.isSwitch()) {
            this.context.addResolvedArgument(parameter.name(), true);
            return CompletionResult.CONTINUE;
        }
        try {
            Object value = parameter.parse(this.input, this.context);
            this.context.addResolvedArgument(parameter.name(), value);
            int positionAfterParsing = this.input.position();
            String consumed = this.restorePosition();
            Collection<String> parameterSuggestions = parameter.complete(this.context);
            this.input.setPosition(positionAfterParsing);
            if (this.input.hasFinished()) {
                this.filterSuggestions(consumed, parameterSuggestions);
                return CompletionResult.HALT;
            }
            if (this.input.peek() == ' ') {
                this.input.skipWhitespace();
            }
            return CompletionResult.CONTINUE;
        } catch (Throwable t) {
            String consumed = this.restorePosition();
            this.filterSuggestions(consumed, parameter.complete(this.context));
            return CompletionResult.HALT;
        }
    }

    @Contract(mutates="param1")
    private void completeFlags(@NotNull Map<String, ParameterNode<A, Object>> remainingFlags) {
        boolean lastWasShort = false;
        while (this.input.hasRemaining()) {
            String next;
            if (this.input.peek() == ' ') {
                this.input.skipWhitespace();
            }
            if ((next = this.input.peekUnquotedString()).startsWith("--")) {
                lastWasShort = false;
                String flagName = next.substring("--".length());
                ParameterNode<A, Object> targetFlag = remainingFlags.remove(flagName);
                if (targetFlag == null) {
                    for (ParameterNode parameterNode : remainingFlags.values()) {
                        if (!this.universalFlagName(parameterNode).startsWith(flagName)) continue;
                        this.suggestions.add("--" + this.universalFlagName(parameterNode));
                    }
                    return;
                }
                this.input.readUnquotedString();
                if (this.input.hasFinished()) {
                    return;
                }
                if (this.input.remaining() == 1 && this.input.peek() == ' ') {
                    Collection<String> parameterSuggestions = targetFlag.complete(this.context);
                    this.suggestions.addAll(parameterSuggestions);
                    return;
                }
                this.input.skipWhitespace();
                CompletionResult result = this.completeParameter(targetFlag);
                if (result == CompletionResult.HALT) {
                    return;
                }
                if (!this.input.hasRemaining() || this.input.peek() != ' ') continue;
                this.input.skipWhitespace();
                continue;
            }
            if (!next.startsWith("-")) continue;
            lastWasShort = true;
            String shortenedString = next.substring("-".length());
            char[] spec = shortenedString.toCharArray();
            this.input.moveForward("-".length());
            for (Object flag : (Object)spec) {
                CompletionResult result;
                this.input.moveForward();
                @Nullable ParameterNode<A, Object> targetFlag = this.removeParameterWithShorthand(remainingFlags, (char)flag);
                if (targetFlag == null) continue;
                if (targetFlag.isSwitch()) {
                    this.context.addResolvedArgument(targetFlag.name(), true);
                }
                if (this.input.hasFinished()) {
                    if (targetFlag.isFlag()) {
                        return;
                    }
                    for (ParameterNode<A, Object> remFlag : remainingFlags.values()) {
                        if (remFlag.shorthand() == null) continue;
                        String flagCompletion = "-" + shortenedString + remFlag.shorthand();
                        this.suggestions.add(remFlag.isFlag() ? flagCompletion + ' ' : flagCompletion);
                    }
                    return;
                }
                if (targetFlag.isSwitch()) continue;
                if (this.input.remaining() == 1 && this.input.peek() == ' ') {
                    Collection<String> parameterSuggestions = targetFlag.complete(this.context);
                    this.suggestions.addAll(parameterSuggestions);
                    return;
                }
                if (this.input.hasRemaining() && this.input.peek() == ' ') {
                    this.input.skipWhitespace();
                }
                if ((result = this.completeParameter(targetFlag)) != CompletionResult.HALT) continue;
                return;
            }
        }
        for (ParameterNode<A, Object> c : remainingFlags.values()) {
            if (lastWasShort) {
                this.suggestions.add("-" + c.shorthand());
                continue;
            }
            this.suggestions.add("--" + (c.isSwitch() ? c.switchName() : c.flagName()));
        }
    }

    @Nullable
    private ParameterNode<A, Object> removeParameterWithShorthand(Map<String, ParameterNode<A, Object>> parametersLeft, char c) {
        Iterator<Map.Entry<String, ParameterNode<A, Object>>> iterator = parametersLeft.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ParameterNode<A, Object>> entry = iterator.next();
            Character shorthand = entry.getValue().shorthand();
            if (shorthand == null || shorthand.charValue() != c) continue;
            iterator.remove();
            return entry.getValue();
        }
        return null;
    }

    private CompletionResult completeLiteral(@NotNull LiteralNode<A> node) {
        String nextWord = this.input.readUnquotedString();
        if (this.input.hasFinished()) {
            if (node.name().startsWith(nextWord)) {
                this.suggestions.add(node.name());
            }
            return CompletionResult.HALT;
        }
        if (!node.name().equalsIgnoreCase(nextWord)) {
            return CompletionResult.HALT;
        }
        if (this.input.hasRemaining() && this.input.peek() == ' ') {
            this.input.skipWhitespace();
            return CompletionResult.CONTINUE;
        }
        return CompletionResult.HALT;
    }

    private void filterSuggestions(String consumed, @NotNull Collection<String> parameterSuggestions) {
        for (String parameterSuggestion : parameterSuggestions) {
            if (!parameterSuggestion.toLowerCase().startsWith(consumed.toLowerCase())) continue;
            this.suggestions.add(SingleCommandCompleter.getRemainingContent(parameterSuggestion, consumed));
        }
    }

    private String universalFlagName(@NotNull ParameterNode<A, Object> parameter) {
        if (parameter.isSwitch()) {
            return parameter.switchName();
        }
        if (parameter.isFlag()) {
            return parameter.flagName();
        }
        return parameter.name();
    }

    @NotNull
    public List<String> suggestions() {
        return this.suggestions;
    }

    private static String getRemainingContent(String suggestion, String consumed) {
        int matchIndex = consumed.length();
        int spaceIndex = suggestion.lastIndexOf(32, matchIndex - 1);
        return suggestion.substring(spaceIndex + 1);
    }

    private static enum CompletionResult {
        HALT,
        CONTINUE;

    }
}

