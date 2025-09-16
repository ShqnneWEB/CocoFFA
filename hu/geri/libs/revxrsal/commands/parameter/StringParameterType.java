/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.parameter.PrioritySpec;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import org.jetbrains.annotations.NotNull;

public final class StringParameterType
implements ParameterType<CommandActor, String> {
    private static final StringParameterType GREEDY = new StringParameterType(true);
    private static final StringParameterType SINGLE = new StringParameterType(false);
    private final boolean greedy;

    private StringParameterType(boolean greedy) {
        this.greedy = greedy;
    }

    @NotNull
    public static <A extends CommandActor> ParameterType<A, String> greedy() {
        return GREEDY;
    }

    @NotNull
    public static <A extends CommandActor> ParameterType<A, String> single() {
        return SINGLE;
    }

    @Override
    public String parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<CommandActor> context) {
        if (input.peek() == '\"' || !this.greedy) {
            return input.readString();
        }
        return input.consumeRemaining();
    }

    @Override
    public boolean isGreedy() {
        return this.greedy;
    }

    @Override
    @NotNull
    public PrioritySpec parsePriority() {
        return PrioritySpec.lowest();
    }
}

