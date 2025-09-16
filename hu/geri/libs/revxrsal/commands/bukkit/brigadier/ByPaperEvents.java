/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.command.UnknownCommandEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package hu.geri.libs.revxrsal.commands.bukkit.brigadier;

import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.brigadier.BrigadierConverter;
import hu.geri.libs.revxrsal.commands.brigadier.BrigadierParser;
import hu.geri.libs.revxrsal.commands.brigadier.types.ArgumentTypes;
import hu.geri.libs.revxrsal.commands.bukkit.actor.ActorFactory;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.bukkit.brigadier.BrigadierUtil;
import hu.geri.libs.revxrsal.commands.bukkit.brigadier.BukkitBrigadierBridge;
import hu.geri.libs.revxrsal.commands.bukkit.hooks.LampCommandExecutor;
import hu.geri.libs.revxrsal.commands.bukkit.util.PluginCommands;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import hu.geri.libs.revxrsal.commands.util.Strings;
import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

final class ByPaperEvents<A extends BukkitCommandActor>
implements BukkitBrigadierBridge<A>,
BrigadierConverter<A, Object>,
Listener {
    private final RootCommandNode<Object> rootNode = new RootCommandNode();
    private final String fallbackPrefix;
    private final ArgumentTypes<A> types;
    private final ActorFactory<A> actorFactory;
    private final JavaPlugin plugin;
    private final BrigadierParser<Object, A> parser = new BrigadierParser(this);
    private boolean unknownCommandListenerRegistered = false;

    ByPaperEvents(@NotNull JavaPlugin plugin, ArgumentTypes<A> types, @NotNull ActorFactory<A> actorFactory) {
        this.plugin = plugin;
        this.fallbackPrefix = plugin.getName().toLowerCase().trim();
        this.types = types;
        this.actorFactory = actorFactory;
        this.registerListener((Plugin)plugin);
    }

    private void registerListener(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents((Listener)new CommandRegisterListener(), plugin);
    }

    @Override
    @NotNull
    public ArgumentType<?> getArgumentType(@NotNull ParameterNode<A, ?> parameter) {
        return this.types.type(parameter);
    }

    @Override
    @NotNull
    public A createActor(@NotNull Object sender, @NotNull Lamp<A> lamp) {
        return this.actorFactory.create(BrigadierUtil.getBukkitSender(sender), lamp);
    }

    @Override
    public void register(ExecutableCommand<A> command) {
        Objects.requireNonNull(command, "command");
        if (!this.unknownCommandListenerRegistered) {
            Bukkit.getPluginManager().registerEvents((Listener)new UnknownCommandListener(command.lamp()), (Plugin)this.plugin);
            this.unknownCommandListenerRegistered = true;
        }
        LiteralCommandNode<Object> node = this.parser.createNode(command);
        List<String> aliases = BukkitBrigadierBridge.getAliases((Command)PluginCommands.getCommand(this.plugin, command.firstNode().name()));
        if (!aliases.contains(node.getLiteral())) {
            node = BrigadierUtil.renameLiteralNode(node, command.firstNode().name());
        }
        for (String alias : aliases) {
            if (node.getLiteral().equals(alias)) {
                BrigadierParser.addChild(this.rootNode, node);
                continue;
            }
            LiteralCommandNode redirectNode = ((LiteralArgumentBuilder)LiteralArgumentBuilder.literal((String)alias).redirect(node)).build();
            BrigadierParser.addChild(this.rootNode, redirectNode);
        }
    }

    public final class CommandRegisterListener
    implements Listener {
        @EventHandler
        public void onCommandRegistered(CommandRegisteredEvent<?> event) {
            if (!(event.getCommand() instanceof PluginCommand)) {
                return;
            }
            PluginCommand pCommand = (PluginCommand)event.getCommand();
            if (!(pCommand.getExecutor() instanceof LampCommandExecutor)) {
                return;
            }
            LiteralCommandNode node = (LiteralCommandNode)ByPaperEvents.this.rootNode.getChild(event.getCommandLabel());
            if (node != null) {
                event.setLiteral(node);
            }
        }
    }

    public final class UnknownCommandListener
    implements Listener {
        private final Lamp<A> lamp;

        public UnknownCommandListener(Lamp<A> lamp) {
            this.lamp = lamp;
        }

        @EventHandler
        public void onUnknownCommand(UnknownCommandEvent event) {
            if (event.getCommandLine().isEmpty()) {
                return;
            }
            MutableStringStream input = StringStream.createMutable(Strings.stripNamespace(ByPaperEvents.this.fallbackPrefix, event.getCommandLine()));
            if (ByPaperEvents.this.rootNode.getChild(input.peekUnquotedString()) != null) {
                event.setMessage(null);
                Object actor = ByPaperEvents.this.actorFactory.create(event.getSender(), this.lamp);
                this.lamp.dispatch(actor, input);
            }
        }
    }
}

