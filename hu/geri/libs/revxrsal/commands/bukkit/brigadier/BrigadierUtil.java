/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  org.bukkit.command.CommandSender
 */
package hu.geri.libs.revxrsal.commands.bukkit.brigadier;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import hu.geri.libs.revxrsal.commands.brigadier.BrigadierParser;
import hu.geri.libs.revxrsal.commands.bukkit.util.BukkitVersion;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

final class BrigadierUtil {
    private static final Field CHILDREN_FIELD;
    private static final Field LITERALS_FIELD;
    private static final Field ARGUMENTS_FIELD;
    private static final Method GET_BUKKIT_SENDER_METHOD;
    private static final Field[] CHILDREN_FIELDS;

    BrigadierUtil() {
    }

    public static void removeChild(RootCommandNode root, String name) {
        try {
            for (Field field : CHILDREN_FIELDS) {
                Map children = (Map)field.get(root);
                children.remove(name);
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static <S> LiteralCommandNode<S> renameLiteralNode(LiteralCommandNode<S> node, String newLiteral) {
        LiteralCommandNode clone = new LiteralCommandNode(newLiteral, node.getCommand(), node.getRequirement(), node.getRedirect(), node.getRedirectModifier(), node.isFork());
        for (CommandNode child : node.getChildren()) {
            BrigadierParser.addChild(clone, child);
        }
        return clone;
    }

    @NotNull
    public static CommandSender getBukkitSender(Object commandSource) {
        Objects.requireNonNull(commandSource, "commandSource");
        try {
            return (CommandSender)GET_BUKKIT_SENDER_METHOD.invoke(commandSource, new Object[0]);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            Class<?> commandListenerWrapper = BukkitVersion.findNmsClass("commands.CommandSourceStack", "commands.CommandListenerWrapper", "command.CommandListenerWrapper", "CommandListenerWrapper");
            CHILDREN_FIELD = CommandNode.class.getDeclaredField("children");
            LITERALS_FIELD = CommandNode.class.getDeclaredField("literals");
            ARGUMENTS_FIELD = CommandNode.class.getDeclaredField("arguments");
            for (Field field : CHILDREN_FIELDS = new Field[]{CHILDREN_FIELD, LITERALS_FIELD, ARGUMENTS_FIELD}) {
                field.setAccessible(true);
            }
            GET_BUKKIT_SENDER_METHOD = commandListenerWrapper.getDeclaredMethod("getBukkitSender", new Class[0]);
            GET_BUKKIT_SENDER_METHOD.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}

