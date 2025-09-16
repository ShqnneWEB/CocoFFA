/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.DefaultFailureHandler;
import hu.geri.libs.revxrsal.commands.node.FailureHandler;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import hu.geri.libs.revxrsal.commands.util.StackTraceSanitizer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public final class DispatcherSettings<A extends CommandActor> {
    public static final String LONG_FORMAT_PREFIX = "--";
    public static final String SHORT_FORMAT_PREFIX = "-";
    public static final int DEFAULT_MAXIMUM_FAILED_ATTEMPTS = 5;
    private final int maximumFailedAttempts;
    @NotNull
    private final FailureHandler<A> failureHandler;
    @NotNull
    private final StackTraceSanitizer stackTraceSanitizer;

    private DispatcherSettings(Builder<A> builder) {
        this.maximumFailedAttempts = ((Builder)builder).maximumFailedAttempts;
        this.failureHandler = ((Builder)builder).failureHandler;
        this.stackTraceSanitizer = ((Builder)builder).stackTraceSanitizer;
    }

    @NotNull
    public static <A extends CommandActor> Builder<A> builder() {
        return new Builder();
    }

    public int maximumFailedAttempts() {
        return this.maximumFailedAttempts;
    }

    @NotNull
    public FailureHandler<A> failureHandler() {
        return this.failureHandler;
    }

    @NotNull
    public StackTraceSanitizer stackTraceSanitizer() {
        return this.stackTraceSanitizer;
    }

    @Contract(value="-> new", pure=true)
    @NotNull
    public Builder<A> toBuilder() {
        return new Builder().maximumFailedAttempts(this.maximumFailedAttempts).failureHandler(this.failureHandler);
    }

    public static final class Builder<A extends CommandActor> {
        private int maximumFailedAttempts = 5;
        private FailureHandler<A> failureHandler = DefaultFailureHandler.defaultFailureHandler();
        @NotNull
        private StackTraceSanitizer stackTraceSanitizer = StackTraceSanitizer.defaultSanitizer();

        @NotNull
        public Builder<A> maximumFailedAttempts(@Range(from=1L, to=0x7FFFFFFFL) int maximumFailedAttempts) {
            if (maximumFailedAttempts < 0) {
                throw new IllegalArgumentException("Maximum failed attempts cannot be a negative number!");
            }
            this.maximumFailedAttempts = maximumFailedAttempts;
            return this;
        }

        @Contract(value="null -> fail")
        @NotNull
        public Builder<A> failureHandler(FailureHandler<? super A> failureHandler) {
            Preconditions.notNull(failureHandler, "failure handler");
            this.failureHandler = failureHandler;
            return this;
        }

        public Builder<A> stackTraceSanitizer(@NotNull StackTraceSanitizer stackTraceSanitizer) {
            this.stackTraceSanitizer = Preconditions.notNull(stackTraceSanitizer, "stack trace sanitizer");
            return this;
        }

        @Contract(value="-> new", pure=true)
        @NotNull
        public DispatcherSettings<A> build() {
            return new DispatcherSettings(this);
        }
    }
}

