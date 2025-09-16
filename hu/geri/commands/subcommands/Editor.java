/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package hu.geri.commands.subcommands;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.guis.MainEditorGUI;
import org.bukkit.entity.Player;

public enum Editor {
    INSTANCE;


    public void execute(Player player, String arenaName) {
        CocoFFA plugin = CocoFFA.getInstance();
        Arena targetArena = plugin.getArenaManager().getArena(arenaName);
        if (targetArena == null) {
            player.sendMessage(plugin.getLocaleManager().getMessage("commands.join.not-found", "{arena_name}", arenaName));
            return;
        }
        new MainEditorGUI(plugin).openMainEditor(player, targetArena);
    }
}

