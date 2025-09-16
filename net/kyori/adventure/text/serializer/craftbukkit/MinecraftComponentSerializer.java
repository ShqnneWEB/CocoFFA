/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.serializer.craftbukkit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

@Deprecated
public final class MinecraftComponentSerializer
implements ComponentSerializer<Component, Component, Object> {
    private static final MinecraftComponentSerializer INSTANCE = new MinecraftComponentSerializer();
    private final net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer realSerial = net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer.get();

    public static boolean isSupported() {
        return net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer.isSupported();
    }

    @NotNull
    public static MinecraftComponentSerializer get() {
        return INSTANCE;
    }

    @Override
    @NotNull
    public Component deserialize(@NotNull Object input) {
        return this.realSerial.deserialize(input);
    }

    @Override
    @NotNull
    public Object serialize(@NotNull Component component) {
        return this.realSerial.serialize(component);
    }
}

