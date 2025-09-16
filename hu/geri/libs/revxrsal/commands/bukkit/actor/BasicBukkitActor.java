/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.libs.revxrsal.commands.bukkit.actor;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.process.MessageSender;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

final class BasicBukkitActor
implements BukkitCommandActor {
    private static final UUID CONSOLE_UUID = new UUID(0L, 0L);
    private final CommandSender sender;
    private final Plugin plugin;
    private final Optional<BukkitAudiences> audiences;
    private final MessageSender<BukkitCommandActor, ComponentLike> messageSender;
    private final Lamp<BukkitCommandActor> lamp;

    BasicBukkitActor(CommandSender sender, Plugin plugin, Optional<BukkitAudiences> audiences, MessageSender<BukkitCommandActor, ComponentLike> messageSender, Lamp<BukkitCommandActor> lamp) {
        this.sender = sender;
        this.plugin = plugin;
        this.audiences = audiences;
        this.messageSender = messageSender;
        this.lamp = lamp;
    }

    @Override
    @NotNull
    public CommandSender sender() {
        return this.sender;
    }

    @Override
    public void reply(@NotNull ComponentLike message) {
        if (this.messageSender == null) {
            this.audience().ifPresent(a -> a.sendMessage(message));
        } else {
            this.messageSender.send(this, message);
        }
    }

    @Override
    @NotNull
    public Optional<Audience> audience() {
        if (this.sender instanceof Audience) {
            return Optional.of((Audience)this.sender);
        }
        if (!this.audiences.isPresent()) {
            return Optional.empty();
        }
        BukkitAudiences bukkitAudiences = this.audiences.get();
        return Optional.of(bukkitAudiences.sender(this.sender()));
    }

    @Override
    @NotNull
    public UUID uniqueId() {
        if (this.isPlayer()) {
            return ((Player)this.sender).getUniqueId();
        }
        if (this.isConsole()) {
            return CONSOLE_UUID;
        }
        return UUID.nameUUIDFromBytes(this.name().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Lamp<BukkitCommandActor> lamp() {
        return this.lamp;
    }

    public Plugin plugin() {
        return this.plugin;
    }

    public Optional<BukkitAudiences> audiences() {
        return this.audiences;
    }

    public MessageSender<BukkitCommandActor, ComponentLike> messageSender() {
        return this.messageSender;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        BasicBukkitActor that = (BasicBukkitActor)obj;
        return Objects.equals(this.sender, that.sender) && Objects.equals(this.plugin, that.plugin) && Objects.equals(this.audiences, that.audiences) && Objects.equals(this.messageSender, that.messageSender) && Objects.equals(this.lamp, that.lamp);
    }

    public int hashCode() {
        return Objects.hash(this.sender, this.plugin, this.audiences, this.messageSender, this.lamp);
    }

    public String toString() {
        return "BasicBukkitActor[sender=" + this.sender + ", plugin=" + this.plugin + ", audiences=" + this.audiences + ", messageSender=" + this.messageSender + ", lamp=" + this.lamp + ']';
    }
}

