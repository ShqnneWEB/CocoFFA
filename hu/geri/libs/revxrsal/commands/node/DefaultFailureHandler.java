/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.Potential;
import hu.geri.libs.revxrsal.commands.exception.NoPermissionException;
import hu.geri.libs.revxrsal.commands.node.FailureHandler;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

final class DefaultFailureHandler<A extends CommandActor>
implements FailureHandler<A> {
    private static final int MAX_NUMBER_OF_SUGGESTIONS = 6;
    private static final DefaultFailureHandler<CommandActor> INSTANCE = new DefaultFailureHandler();

    DefaultFailureHandler() {
    }

    public static <A extends CommandActor> FailureHandler<A> defaultFailureHandler() {
        return INSTANCE;
    }

    @Override
    public void handleFailedAttempts(@NotNull A actor, @NotNull @Unmodifiable List<Potential<A>> failedAttempts, @NotNull StringStream input) {
        if (failedAttempts.isEmpty()) {
            return;
        }
        if (failedAttempts.size() == 1) {
            failedAttempts.get(0).handleException();
            return;
        }
        if (failedAttempts.get(0).error() instanceof NoPermissionException) {
            failedAttempts.get(0).handleException();
            return;
        }
        actor.error("Failed to find a suitable command for your input (\"" + input.source() + "\"). Did you mean:");
        for (int i = 0; i < failedAttempts.size() && i < 6; ++i) {
            Potential<A> failedAttempt = failedAttempts.get(i);
            actor.reply("- " + failedAttempt.context().command().path());
        }
    }
}

