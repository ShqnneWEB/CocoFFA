/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.serializer.craftbukkit;

import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

@Deprecated
public final class BukkitComponentSerializer {
    private BukkitComponentSerializer() {
    }

    @NotNull
    public static LegacyComponentSerializer legacy() {
        return net.kyori.adventure.platform.bukkit.BukkitComponentSerializer.legacy();
    }

    @NotNull
    public static GsonComponentSerializer gson() {
        return net.kyori.adventure.platform.bukkit.BukkitComponentSerializer.gson();
    }
}

