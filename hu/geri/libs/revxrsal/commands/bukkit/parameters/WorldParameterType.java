/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 */
package hu.geri.libs.revxrsal.commands.bukkit.parameters;

import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.bukkit.exception.InvalidWorldException;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public final class WorldParameterType
implements ParameterType<BukkitCommandActor, World> {
    @Override
    public World parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String name = input.readString();
        if (name.equals("self") || name.equals("me") || name.equals("mine")) {
            return context.actor().requirePlayer().getWorld();
        }
        World world = Bukkit.getWorld((String)name);
        if (world != null) {
            return world;
        }
        throw new InvalidWorldException(name);
    }

    @Override
    @NotNull
    public SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        return context -> Collections.map(Bukkit.getWorlds(), World::getName);
    }
}

