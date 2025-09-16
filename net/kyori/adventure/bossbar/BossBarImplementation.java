/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.bossbar;

import java.util.Collections;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBarImpl;
import net.kyori.adventure.bossbar.BossBarViewer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface BossBarImplementation {
    @ApiStatus.Internal
    @NotNull
    public static <I extends BossBarImplementation> I get(@NotNull BossBar bar, @NotNull Class<I> type) {
        return BossBarImpl.ImplementationAccessor.get(bar, type);
    }

    @ApiStatus.Internal
    @NotNull
    default public Iterable<? extends BossBarViewer> viewers() {
        return Collections.emptyList();
    }

    @ApiStatus.Internal
    public static interface Provider {
        @ApiStatus.Internal
        @NotNull
        public BossBarImplementation create(@NotNull BossBar var1);
    }
}

