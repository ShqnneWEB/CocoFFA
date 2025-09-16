/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node.parser;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.InputParseException;
import hu.geri.libs.revxrsal.commands.exception.UnknownParameterException;
import hu.geri.libs.revxrsal.commands.exception.context.ErrorContext;
import hu.geri.libs.revxrsal.commands.node.MutableExecutionContext;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.reflect.ktx.KotlinConstants;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import hu.geri.libs.revxrsal.commands.util.Collections;
import hu.geri.libs.revxrsal.commands.util.Strings;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

final class FlagParser<A extends CommandActor> {
    private final MutableExecutionContext<A> context;
    private final List<ParameterNode<A, Object>> parametersLeft;
    private final MutableStringStream input;
    private final List<Strings.StringRange> rangesToRemove = new ArrayList<Strings.StringRange>();
    private Throwable error;
    private ErrorContext<A> errorContext;

    public FlagParser(MutableExecutionContext<A> context, MutableStringStream input) {
        this.context = context;
        this.parametersLeft = Collections.filter(context.command().parameters().values(), parameter -> parameter.isSwitch() || parameter.isFlag());
        this.input = input;
    }

    public boolean tryParse() {
        try {
            while (this.input.hasRemaining()) {
                String next;
                int start = this.input.position();
                if (this.input.peek() == ' ') {
                    this.input.skipWhitespace();
                }
                if ((next = this.input.peekUnquotedString()).startsWith("--")) {
                    String flagName = next.substring("--".length());
                    ParameterNode<A, Object> parameter = this.removeParameterNamed(flagName);
                    this.input.readUnquotedString();
                    this.parseNext(this.context, parameter);
                    int end = this.input.position();
                    this.rangesToRemove.add(new Strings.StringRange(start, end));
                    continue;
                }
                if (next.startsWith("-")) {
                    this.input.readUnquotedString();
                    char[] flags = next.substring("-".length()).toCharArray();
                    for (Object flag : (ParameterNode<A, Object>)flags) {
                        ParameterNode<A, Object> parameter = this.removeParameterWithShorthand((char)flag);
                        this.parseNext(this.context, parameter);
                    }
                    int end = this.input.position();
                    this.rangesToRemove.add(new Strings.StringRange(start, end));
                    continue;
                }
                this.input.moveForward(next.length());
            }
            for (ParameterNode<A, Object> parameter : this.parametersLeft) {
                if (parameter.isSwitch()) {
                    if (KotlinConstants.isKotlinClass(parameter.command().function().method().getDeclaringClass())) continue;
                    this.context.addResolvedArgument(parameter.name(), false);
                    continue;
                }
                if (!parameter.isFlag()) continue;
                this.parseFlag(this.context, parameter, StringStream.createMutable(""));
            }
            return true;
        } catch (Throwable t) {
            this.error = t;
            return false;
        }
    }

    private void parseNext(MutableExecutionContext<A> context, ParameterNode<A, Object> parameter) {
        if (parameter.isSwitch()) {
            context.addResolvedArgument(parameter.name(), true);
        } else {
            if (this.input.hasFinished() || this.input.peek() != ' ') {
                throw new InputParseException(InputParseException.Cause.EXPECTED_WHITESPACE);
            }
            this.input.skipWhitespace();
            this.parseFlag(context, parameter, this.input);
        }
    }

    private <T> void parseFlag(MutableExecutionContext<A> context, ParameterNode<A, T> parameter, MutableStringStream input) {
        try {
            T value = parameter.parse(input, context);
            context.addResolvedArgument(parameter.name(), value);
        } catch (Throwable t) {
            this.errorContext = ErrorContext.parsingParameter(context, parameter, input);
            throw t;
        }
    }

    @NotNull
    private ParameterNode<A, Object> removeParameterWithShorthand(char c) {
        Iterator<ParameterNode<A, Object>> iterator = this.parametersLeft.iterator();
        while (iterator.hasNext()) {
            ParameterNode<A, Object> value = iterator.next();
            Character shorthand = value.shorthand();
            if (shorthand == null || shorthand.charValue() != c) continue;
            iterator.remove();
            return value;
        }
        this.errorContext = ErrorContext.unknownParameter(this.context);
        throw new UnknownParameterException(Character.toString(c), true);
    }

    @NotNull
    private ParameterNode<A, Object> removeParameterNamed(String name) {
        Iterator<ParameterNode<A, Object>> iterator = this.parametersLeft.iterator();
        while (iterator.hasNext()) {
            ParameterNode<A, Object> value = iterator.next();
            if (value.isFlag() && Objects.equals(value.flagName(), name)) {
                iterator.remove();
                return value;
            }
            if (!value.isSwitch() || !Objects.equals(value.switchName(), name)) continue;
            iterator.remove();
            return value;
        }
        this.errorContext = ErrorContext.unknownParameter(this.context);
        throw new UnknownParameterException(name, false);
    }

    @NotNull
    public MutableStringStream strippedInput() {
        String string = Strings.removeRanges(this.input.source(), this.rangesToRemove);
        return StringStream.createMutable(string);
    }

    public ErrorContext<A> errorContext() {
        return this.errorContext;
    }

    public Throwable error() {
        return this.error;
    }
}

