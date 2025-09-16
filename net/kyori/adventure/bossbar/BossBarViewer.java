/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.bossbar;

import net.kyori.adventure.bossbar.BossBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public interface BossBarViewer {
    public @UnmodifiableView @NotNull Iterable<? extends BossBar> activeBossBars();
}

