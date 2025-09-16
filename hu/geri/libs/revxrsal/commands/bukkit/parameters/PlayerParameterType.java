/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package hu.geri.libs.revxrsal.commands.bukkit.parameters;

import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.bukkit.exception.EmptyEntitySelectorException;
import hu.geri.libs.revxrsal.commands.bukkit.exception.InvalidPlayerException;
import hu.geri.libs.revxrsal.commands.bukkit.exception.MalformedEntitySelectorException;
import hu.geri.libs.revxrsal.commands.bukkit.exception.MoreThanOneEntityException;
import hu.geri.libs.revxrsal.commands.bukkit.exception.NonPlayerEntitiesException;
import hu.geri.libs.revxrsal.commands.bukkit.util.BukkitVersion;
import hu.geri.libs.revxrsal.commands.exception.CommandErrorException;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PlayerParameterType
implements ParameterType<BukkitCommandActor, Player> {
    private final boolean brigadierEnabled;

    public PlayerParameterType(boolean brigadierEnabled) {
        this.brigadierEnabled = brigadierEnabled;
    }

    @NotNull
    private static Player fromSelector(@NotNull CommandSender sender, @NotNull String selector) {
        try {
            List entityList = Bukkit.selectEntities((CommandSender)sender, (String)selector);
            if (entityList.isEmpty()) {
                throw new EmptyEntitySelectorException(selector);
            }
            if (entityList.size() != 1) {
                throw new MoreThanOneEntityException(selector);
            }
            Entity entity = (Entity)entityList.get(0);
            if (!(entity instanceof Player)) {
                throw new NonPlayerEntitiesException(selector);
            }
            Player player = (Player)entity;
            return player;
        } catch (IllegalArgumentException e) {
            throw new MalformedEntitySelectorException(selector, e.getCause().getMessage());
        } catch (NoSuchMethodError e) {
            throw new CommandErrorException("Entity selectors on legacy versions are not supported yet!", new Object[0]);
        }
    }

    @Override
    public Player parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String value = input.readString();
        if (BukkitVersion.isBrigadierSupported()) {
            return PlayerParameterType.fromSelector(context.actor().sender(), value);
        }
        if (value.equals("self") || value.equals("me") || value.equals("@s")) {
            return context.actor().requirePlayer();
        }
        Player player = Bukkit.getPlayerExact((String)value);
        if (player != null) {
            return player;
        }
        throw new InvalidPlayerException(value);
    }

    @Override
    @NotNull
    public SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        if (BukkitVersion.isBrigadierSupported() && this.brigadierEnabled) {
            return SuggestionProvider.empty();
        }
        return context -> Collections.map(Bukkit.getOnlinePlayers(), OfflinePlayer::getName);
    }
}

