/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  org.bukkit.Location
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package hu.geri.libs.revxrsal.commands.bukkit.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import hu.geri.libs.revxrsal.commands.annotation.Single;
import hu.geri.libs.revxrsal.commands.brigadier.types.ArgumentTypes;
import hu.geri.libs.revxrsal.commands.bukkit.brigadier.MinecraftArgumentType;
import hu.geri.libs.revxrsal.commands.bukkit.parameters.EntitySelector;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.util.Classes;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class BukkitArgumentTypes {
    private static final ArgumentType<?> SINGLE_PLAYER = MinecraftArgumentType.ENTITY.create(true, true);
    private static final ArgumentType<?> PLAYERS = MinecraftArgumentType.ENTITY.create(false, true);
    private static final ArgumentType<?> SINGLE_ENTITY = MinecraftArgumentType.ENTITY.create(true, false);
    private static final ArgumentType<?> ENTITIES = MinecraftArgumentType.ENTITY.create(false, false);

    @Contract(value="-> new", pure=true)
    @NotNull
    public static <A extends CommandActor> ArgumentTypes.Builder<A> builder() {
        ArgumentTypes.Builder builder = ArgumentTypes.builder();
        MinecraftArgumentType.UUID.getIfPresent().ifPresent(uuid -> builder.addTypeLast(UUID.class, (ArgumentType<?>)uuid));
        return builder.addTypeLast(OfflinePlayer.class, SINGLE_PLAYER).addTypeLast(Player.class, SINGLE_PLAYER).addTypeLast(Entity.class, SINGLE_ENTITY).addTypeLast(Location.class, MinecraftArgumentType.BLOCK_POS.get()).addTypeFactoryLast(parameter -> {
            if (parameter.type() != EntitySelector.class) {
                return null;
            }
            Class<Entity> entityType = Classes.getRawType(Classes.getFirstGeneric(parameter.fullType(), Entity.class)).asSubclass(Entity.class);
            boolean single = parameter.annotations().contains(Single.class);
            if (entityType == Player.class) {
                return single ? SINGLE_PLAYER : PLAYERS;
            }
            return single ? SINGLE_ENTITY : ENTITIES;
        });
    }
}

