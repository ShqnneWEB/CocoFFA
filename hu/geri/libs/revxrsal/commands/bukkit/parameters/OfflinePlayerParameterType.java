/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 */
package hu.geri.libs.revxrsal.commands.bukkit.parameters;

import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.bukkit.exception.InvalidPlayerException;
import hu.geri.libs.revxrsal.commands.bukkit.util.BukkitVersion;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public final class OfflinePlayerParameterType
implements ParameterType<BukkitCommandActor, OfflinePlayer> {
    private final boolean brigadierEnabled;

    public OfflinePlayerParameterType(boolean brigadierEnabled) {
        this.brigadierEnabled = brigadierEnabled;
    }

    private static boolean exists(OfflinePlayer player) {
        return player.hasPlayedBefore() || player.isOnline() || player.getFirstPlayed() != 0L;
    }

    @Override
    public OfflinePlayer parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String name = input.readString();
        if (name.equals("self") || name.equals("me") || name.equals("@s")) {
            return context.actor().requirePlayer();
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer((String)name);
        if (OfflinePlayerParameterType.exists(player)) {
            return player;
        }
        throw new InvalidPlayerException(name);
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

