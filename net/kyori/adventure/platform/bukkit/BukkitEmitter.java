/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 */
package net.kyori.adventure.platform.bukkit;

import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Entity;

final class BukkitEmitter
implements Sound.Emitter {
    final Entity entity;

    BukkitEmitter(Entity entity) {
        this.entity = entity;
    }
}

