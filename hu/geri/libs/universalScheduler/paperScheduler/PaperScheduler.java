/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.libs.universalScheduler.paperScheduler;

import hu.geri.libs.universalScheduler.foliaScheduler.FoliaScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PaperScheduler
extends FoliaScheduler {
    public PaperScheduler(Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean isGlobalThread() {
        return Bukkit.getServer().isPrimaryThread();
    }
}

