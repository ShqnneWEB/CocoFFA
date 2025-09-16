/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  io.papermc.paper.command.brigadier.CommandSourceStack
 *  io.papermc.paper.command.brigadier.Commands
 *  io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
 *  io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType
 *  io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package hu.geri.libs.revxrsal.commands.bukkit.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.brigadier.BrigadierConverter;
import hu.geri.libs.revxrsal.commands.brigadier.BrigadierParser;
import hu.geri.libs.revxrsal.commands.brigadier.types.ArgumentTypes;
import hu.geri.libs.revxrsal.commands.bukkit.actor.ActorFactory;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.bukkit.brigadier.BukkitBrigadierBridge;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.lang.reflect.Method;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

final class ByPaperLifecycle<A extends BukkitCommandActor>
implements BukkitBrigadierBridge<A>,
BrigadierConverter<A, CommandSourceStack> {
    private static final Method GET_LIFECYCLE_MANAGER;
    private final ArgumentTypes<A> types;
    private final ActorFactory<A> actorFactory;
    private final RootCommandNode<CommandSourceStack> root = new RootCommandNode();
    private final BrigadierParser<CommandSourceStack, A> parser = new BrigadierParser(this);

    public ByPaperLifecycle(JavaPlugin plugin, ArgumentTypes<A> types, ActorFactory<A> actorFactory) {
        this.types = types;
        this.actorFactory = actorFactory;
        ByPaperLifecycle.getLifecycleManager(plugin).registerEventHandler((LifecycleEventType)LifecycleEvents.COMMANDS, event -> {
            for (CommandNode node : this.root.getChildren()) {
                ((Commands)event.registrar()).register((LiteralCommandNode)node);
            }
        });
    }

    private static LifecycleEventManager<Plugin> getLifecycleManager(JavaPlugin plugin) {
        if (GET_LIFECYCLE_MANAGER == null) {
            throw new IllegalArgumentException("getLifecycleManager is not available.");
        }
        return (LifecycleEventManager)GET_LIFECYCLE_MANAGER.invoke(plugin, new Object[0]);
    }

    @Override
    public void register(ExecutableCommand<A> command) {
        LiteralCommandNode<CommandSourceStack> node = this.parser.createNode(command);
        BrigadierParser.addChild(this.root, node);
    }

    @Override
    @NotNull
    public ArgumentType<?> getArgumentType(@NotNull ParameterNode<A, ?> parameter) {
        return this.types.type(parameter);
    }

    @Override
    @NotNull
    public A createActor(@NotNull CommandSourceStack sender, @NotNull Lamp<A> lamp) {
        return this.actorFactory.create((CommandSender)(sender.getExecutor() == null ? sender.getSender() : sender.getExecutor()), lamp);
    }

    static {
        Method getLifecycleManager;
        try {
            getLifecycleManager = Plugin.class.getDeclaredMethod("getLifecycleManager", new Class[0]);
        } catch (NoSuchMethodException e) {
            getLifecycleManager = null;
        }
        GET_LIFECYCLE_MANAGER = getLifecycleManager;
    }
}

