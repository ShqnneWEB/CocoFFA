/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package hu.geri.managers;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.libs.universalScheduler.scheduling.tasks.MyScheduledTask;
import hu.geri.processor.MessageProcessor;
import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class ActionBarManager {
    private final CocoFFA plugin;
    private final Map<Arena, MyScheduledTask> actionBarTasks;

    public ActionBarManager(CocoFFA plugin) {
        this.plugin = plugin;
        this.actionBarTasks = new HashMap<Arena, MyScheduledTask>();
    }

    public void startInGameActionBar(Arena arena) {
        if (!arena.isInGameActionBarEnabled()) {
            return;
        }
        this.stopInGameActionBar(arena);
        MyScheduledTask task = this.plugin.getUniversalScheduler().runTaskTimer(() -> this.updateActionBar(arena), 0L, 20L);
        this.actionBarTasks.put(arena, task);
    }

    private void updateActionBar(Arena arena) {
        String rawMessage = arena.getInGameActionBarMessage();
        int aliveCount = arena.getPlayers().size();
        for (Player player : arena.getPlayers()) {
            int kills = arena.getPlayerKills(player);
            String message = rawMessage.replace("{alive_players}", String.valueOf(aliveCount)).replace("{kills}", String.valueOf(kills));
            String colored = MessageProcessor.process(message);
            TextComponent comp = LegacyComponentSerializer.legacySection().deserialize(colored);
            player.sendActionBar((Component)comp);
        }
    }

    public void stopInGameActionBar(Arena arena) {
        MyScheduledTask task = this.actionBarTasks.remove(arena);
        if (task != null) {
            task.cancel();
        }
        for (Player player : arena.getPlayers()) {
            player.sendActionBar((Component)Component.empty());
        }
    }

    public void updateSingleActionBar(Player player, Arena arena) {
        if (!arena.isInGameActionBarEnabled()) {
            return;
        }
        String rawMessage = arena.getInGameActionBarMessage();
        int aliveCount = arena.getPlayers().size();
        int kills = arena.getPlayerKills(player);
        String message = rawMessage.replace("{alive_players}", String.valueOf(aliveCount)).replace("{kills}", String.valueOf(kills));
        String colored = MessageProcessor.process(message);
        TextComponent comp = LegacyComponentSerializer.legacySection().deserialize(colored);
        player.sendActionBar((Component)comp);
    }
}

