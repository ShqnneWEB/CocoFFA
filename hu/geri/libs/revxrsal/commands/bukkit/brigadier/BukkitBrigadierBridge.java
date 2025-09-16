/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.PluginCommand
 */
package hu.geri.libs.revxrsal.commands.bukkit.brigadier;

import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

public interface BukkitBrigadierBridge<A extends BukkitCommandActor> {
    @NotNull
    public static List<String> getAliases(Command command) {
        Objects.requireNonNull(command, "command");
        Stream<String> aliasesStream = Stream.concat(Stream.of(command.getLabel()), command.getAliases().stream());
        if (command instanceof PluginCommand) {
            String fallbackPrefix = ((PluginCommand)command).getPlugin().getName().toLowerCase().trim();
            aliasesStream = aliasesStream.flatMap(alias -> Stream.of(alias, fallbackPrefix + ":" + alias));
        }
        return aliasesStream.distinct().collect(Collectors.toList());
    }

    public void register(ExecutableCommand<A> var1);
}

