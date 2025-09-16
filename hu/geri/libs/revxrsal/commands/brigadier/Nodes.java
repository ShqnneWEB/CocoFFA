/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package hu.geri.libs.revxrsal.commands.brigadier;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class Nodes {
    private static final Field COMMAND;
    private static final Field CHILDREN;
    @Nullable
    private static Field LITERALS;
    @Nullable
    private static Field HAS_LITERALS;
    private static final Field ARGUMENTS;
    private static final Field REQUIREMENT;
    private static final Field CUSTOM_SUGGESTIONS;

    Nodes() {
    }

    public static <T> void setCommand(CommandNode<T> node, Command<T> command) {
        try {
            COMMAND.set(node, command);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void setRequirement(@NotNull CommandNode<T> node, @NotNull Predicate<T> requirement) {
        try {
            REQUIREMENT.set(node, requirement);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <S, T> void setSuggestionProvider(ArgumentCommandNode<S, T> node, SuggestionProvider<S> provider) {
        try {
            CUSTOM_SUGGESTIONS.set(node, provider);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static <S> Map<String, CommandNode<S>> getChildren(@NotNull CommandNode<S> node) {
        try {
            return (Map)CHILDREN.get(node);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static <S> Map<String, LiteralCommandNode<S>> getLiterals(@NotNull CommandNode<S> node) {
        if (LITERALS == null) {
            return null;
        }
        try {
            return (Map)LITERALS.get(node);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static <S> Map<String, ArgumentCommandNode<S, ?>> getArguments(@NotNull CommandNode<S> node) {
        try {
            return (Map)ARGUMENTS.get(node);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <S> void setHasLiterals(@NotNull CommandNode<S> node, boolean b) {
        if (HAS_LITERALS == null) {
            return;
        }
        HAS_LITERALS.setBoolean(node, b);
    }

    static {
        try {
            COMMAND = CommandNode.class.getDeclaredField("command");
            COMMAND.setAccessible(true);
            REQUIREMENT = CommandNode.class.getDeclaredField("requirement");
            REQUIREMENT.setAccessible(true);
            CHILDREN = CommandNode.class.getDeclaredField("children");
            CHILDREN.setAccessible(true);
            try {
                LITERALS = CommandNode.class.getDeclaredField("literals");
                LITERALS.setAccessible(true);
            } catch (Throwable t) {
                HAS_LITERALS = CommandNode.class.getDeclaredField("hasLiterals");
                HAS_LITERALS.setAccessible(true);
            }
            ARGUMENTS = CommandNode.class.getDeclaredField("arguments");
            ARGUMENTS.setAccessible(true);
            CUSTOM_SUGGESTIONS = ArgumentCommandNode.class.getDeclaredField("customSuggestions");
            CUSTOM_SUGGESTIONS.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}

