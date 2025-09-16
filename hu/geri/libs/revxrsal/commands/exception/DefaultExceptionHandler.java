/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.CommandInvocationException;
import hu.geri.libs.revxrsal.commands.exception.CooldownException;
import hu.geri.libs.revxrsal.commands.exception.EnumNotFoundException;
import hu.geri.libs.revxrsal.commands.exception.ExpectedLiteralException;
import hu.geri.libs.revxrsal.commands.exception.InputParseException;
import hu.geri.libs.revxrsal.commands.exception.InvalidBooleanException;
import hu.geri.libs.revxrsal.commands.exception.InvalidDecimalException;
import hu.geri.libs.revxrsal.commands.exception.InvalidHelpPageException;
import hu.geri.libs.revxrsal.commands.exception.InvalidIntegerException;
import hu.geri.libs.revxrsal.commands.exception.InvalidListSizeException;
import hu.geri.libs.revxrsal.commands.exception.InvalidStringSizeException;
import hu.geri.libs.revxrsal.commands.exception.InvalidUUIDException;
import hu.geri.libs.revxrsal.commands.exception.MissingArgumentException;
import hu.geri.libs.revxrsal.commands.exception.NoPermissionException;
import hu.geri.libs.revxrsal.commands.exception.NumberNotInRangeException;
import hu.geri.libs.revxrsal.commands.exception.RuntimeExceptionAdapter;
import hu.geri.libs.revxrsal.commands.exception.SendableException;
import hu.geri.libs.revxrsal.commands.exception.UnknownCommandException;
import hu.geri.libs.revxrsal.commands.exception.UnknownParameterException;
import hu.geri.libs.revxrsal.commands.exception.ValueNotAllowedException;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import org.jetbrains.annotations.NotNull;

public class DefaultExceptionHandler<A extends CommandActor>
extends RuntimeExceptionAdapter<A> {
    @RuntimeExceptionAdapter.HandleException
    public void onEnumNotFound(@NotNull EnumNotFoundException e, @NotNull A actor) {
        actor.error("Invalid choice: '" + e.input() + "'. Please enter a valid option from the available values.");
    }

    @RuntimeExceptionAdapter.HandleException
    public void onExpectedLiteral(@NotNull ExpectedLiteralException e, @NotNull A actor) {
        actor.error("Expected '" + e.node().name() + "', found '" + e.input() + "'");
    }

    @RuntimeExceptionAdapter.HandleException
    public void onInputParse(@NotNull InputParseException e, @NotNull A actor) {
        switch (e.cause()) {
            case INVALID_ESCAPE_CHARACTER: {
                actor.error("Invalid input. Use \\\\ to include a backslash.");
                break;
            }
            case UNCLOSED_QUOTE: {
                actor.error("Unclosed quote. Make sure to close all quotes.");
                break;
            }
            case EXPECTED_WHITESPACE: {
                actor.error("Expected whitespace to end one argument, but found trailing data.");
            }
        }
    }

    @RuntimeExceptionAdapter.HandleException
    public void onInvalidListSize(@NotNull InvalidListSizeException e, @NotNull A actor, @NotNull ParameterNode<A, ?> parameter) {
        if (e.inputSize() < e.minimum()) {
            actor.error("You must input at least " + DefaultExceptionHandler.fmt(e.minimum()) + " entries for " + parameter.name());
        }
        if (e.inputSize() > e.maximum()) {
            actor.error("You must input at most " + DefaultExceptionHandler.fmt(e.maximum()) + " entries for " + parameter.name());
        }
    }

    @RuntimeExceptionAdapter.HandleException
    public void onInvalidStringSize(@NotNull InvalidStringSizeException e, @NotNull A actor, @NotNull ParameterNode<A, ?> parameter) {
        if (e.input().length() < e.minimum()) {
            actor.error("Parameter " + parameter.name() + " must be at least " + DefaultExceptionHandler.fmt(e.minimum()) + " characters long.");
        }
        if (e.input().length() > e.maximum()) {
            actor.error("Parameter " + parameter.name() + " can be at most " + DefaultExceptionHandler.fmt(e.maximum()) + " characters long.");
        }
    }

    @RuntimeExceptionAdapter.HandleException
    public void onInvalidBoolean(@NotNull InvalidBooleanException e, @NotNull A actor) {
        actor.error("Expected 'true' or 'false', found " + e.input());
    }

    @RuntimeExceptionAdapter.HandleException
    public void onInvalidDecimal(@NotNull InvalidDecimalException e, @NotNull A actor) {
        actor.error("Invalid number: " + e.input());
    }

    @RuntimeExceptionAdapter.HandleException
    public void onInvalidInteger(@NotNull InvalidIntegerException e, @NotNull A actor) {
        actor.error("Invalid integer: " + e.input());
    }

    @RuntimeExceptionAdapter.HandleException
    public void onInvalidUUID(@NotNull InvalidUUIDException e, @NotNull A actor) {
        actor.error("Invalid UUID: " + e.input());
    }

    @RuntimeExceptionAdapter.HandleException
    public void onMissingArgument(@NotNull MissingArgumentException e, @NotNull A actor, @NotNull ParameterNode<A, ?> parameter) {
        actor.error("Required parameter is missing: " + parameter.name() + ". Usage: " + parameter.command().usage());
    }

    @RuntimeExceptionAdapter.HandleException
    public void onNoPermission(@NotNull NoPermissionException e, @NotNull A actor) {
        actor.error("You do not have permission to execute this command!");
    }

    @RuntimeExceptionAdapter.HandleException
    public void onNumberNotInRange(@NotNull NumberNotInRangeException e, @NotNull A actor, @NotNull ParameterNode<A, Number> parameter) {
        if (e.input().doubleValue() < e.minimum()) {
            actor.error(parameter.name() + " too small (" + DefaultExceptionHandler.fmt(e.input()) + "). Must be at least " + DefaultExceptionHandler.fmt(e.minimum()));
        }
        if (e.input().doubleValue() > e.maximum()) {
            actor.error(parameter.name() + " too large (" + DefaultExceptionHandler.fmt(e.input()) + "). Must be at most " + DefaultExceptionHandler.fmt(e.maximum()));
        }
    }

    @RuntimeExceptionAdapter.HandleException
    public void onUnknownCommand(@NotNull UnknownCommandException e, @NotNull A actor) {
        actor.error("Unknown command: " + e.input());
    }

    @RuntimeExceptionAdapter.HandleException
    public void onInvalidHelpPage(@NotNull InvalidHelpPageException e, @NotNull A actor) {
        if (e.numberOfPages() == 1) {
            actor.error("Invalid help page: " + e.page() + ". Must be 1.");
        } else {
            actor.error("Invalid help page: " + e.page() + ". Must be between 1 and " + e.numberOfPages());
        }
    }

    @RuntimeExceptionAdapter.HandleException
    public void onCommandInvocation(@NotNull CommandInvocationException e, @NotNull A actor) {
        actor.error("An error has occurred while executing this command. Please contact the developers. Errors have been printed to the console.");
        e.cause().printStackTrace();
    }

    @RuntimeExceptionAdapter.HandleException
    public void onUnknownParameter(@NotNull UnknownParameterException e, @NotNull A actor) {
        if (e.shorthand()) {
            actor.error("Unknown shorthand flag: " + e.name());
        } else {
            actor.error("Unknown flag: " + e.name());
        }
    }

    @RuntimeExceptionAdapter.HandleException
    public void onCooldown(@NotNull CooldownException e, @NotNull A actor) {
        actor.error("You must wait " + DefaultExceptionHandler.formatTimeFancy(e.getTimeLeftMillis()) + " before using this command again.");
    }

    @RuntimeExceptionAdapter.HandleException
    public void onValueNotAllowed(@NotNull ValueNotAllowedException e, @NotNull A actor) {
        String allowedValues = String.join((CharSequence)", ", e.allowedValues());
        actor.error("Received an invalid value: " + e.input() + ". Allowed values: " + allowedValues);
    }

    @RuntimeExceptionAdapter.HandleException
    public void onSendable(@NotNull SendableException e, @NotNull A actor) {
        e.sendTo((CommandActor)actor);
    }

    public static String formatTimeFancy(long time) {
        Duration d = Duration.ofMillis(time);
        long hours = d.toHours();
        long minutes = d.minusHours(hours).getSeconds() / 60L;
        long seconds = d.minusMinutes(minutes).minusHours(hours).getSeconds();
        ArrayList<String> words = new ArrayList<String>();
        if (hours != 0L) {
            words.add(hours + DefaultExceptionHandler.plural(hours, " hour"));
        }
        if (minutes != 0L) {
            words.add(minutes + DefaultExceptionHandler.plural(minutes, " minute"));
        }
        if (seconds != 0L) {
            words.add(seconds + DefaultExceptionHandler.plural(seconds, " second"));
        }
        return DefaultExceptionHandler.toFancyString(words);
    }

    public static <T> String toFancyString(List<T> list) {
        StringJoiner builder = new StringJoiner(", ");
        if (list.isEmpty()) {
            return "";
        }
        if (list.size() == 1) {
            return list.get(0).toString();
        }
        for (int i = 0; i < list.size(); ++i) {
            T el = list.get(i);
            if (i + 1 == list.size()) {
                return builder + " and " + el.toString();
            }
            builder.add(el.toString());
        }
        return builder.toString();
    }

    public static String plural(Number count, String thing) {
        if (count.intValue() == 1) {
            return thing;
        }
        if (thing.endsWith("y")) {
            return thing.substring(0, thing.length() - 1) + "ies";
        }
        return thing + "s";
    }
}

