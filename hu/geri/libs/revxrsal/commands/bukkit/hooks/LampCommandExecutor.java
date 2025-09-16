/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabExecutor
 */
package hu.geri.libs.revxrsal.commands.bukkit.hooks;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.bukkit.actor.ActorFactory;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.bukkit.util.BukkitUtils;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import hu.geri.libs.revxrsal.commands.util.Collections;
import hu.geri.libs.revxrsal.commands.util.Strings;
import java.util.List;
import java.util.StringJoiner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

public final class LampCommandExecutor<A extends BukkitCommandActor>
implements TabExecutor {
    @NotNull
    private final Lamp<A> lamp;
    @NotNull
    private final ActorFactory<A> actorFactory;

    public LampCommandExecutor(@NotNull Lamp<A> lamp, @NotNull ActorFactory<A> actorFactory) {
        this.lamp = lamp;
        this.actorFactory = actorFactory;
    }

    private static String ignoreAfterSpace(String v) {
        int spaceIndex = v.indexOf(32);
        return spaceIndex == -1 ? v : v.substring(0, spaceIndex);
    }

    @NotNull
    private static MutableStringStream createInput(String commandName, String[] args) {
        StringJoiner userInput = new StringJoiner(" ");
        userInput.add(Strings.stripNamespace(commandName));
        for (String arg : args) {
            userInput.add(arg);
        }
        return StringStream.createMutable(userInput.toString());
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        A actor = this.actorFactory.create(sender, this.lamp);
        MutableStringStream input = LampCommandExecutor.createInput(command.getName(), args);
        this.lamp.dispatch(actor, input);
        return true;
    }

    @NotNull
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        A actor = this.actorFactory.create(sender, this.lamp);
        MutableStringStream input = LampCommandExecutor.createInput(command.getName(), args);
        List<String> completions = this.lamp.autoCompleter().complete(actor, input);
        if (BukkitUtils.isBrigadierAvailable()) {
            return completions;
        }
        return Collections.map(completions, LampCommandExecutor::ignoreAfterSpace);
    }
}

