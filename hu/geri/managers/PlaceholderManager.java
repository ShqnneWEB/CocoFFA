/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  me.clip.placeholderapi.expansion.PlaceholderExpansion
 *  org.bukkit.OfflinePlayer
 */
package hu.geri.managers;

import hu.geri.CocoFFA;
import hu.geri.leaderboard.ActiveLeaderboard;
import hu.geri.libs.universalScheduler.scheduling.tasks.MyScheduledTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderManager
extends PlaceholderExpansion {
    private final CocoFFA plugin;
    private final Pattern TOPLIST_PATTERN = Pattern.compile("toplist_(\\d+)");
    private ActiveLeaderboard leaderboard;
    private Object updateTask;

    public PlaceholderManager(CocoFFA plugin) {
        this.plugin = plugin;
        this.leaderboard = new ActiveLeaderboard(plugin, null);
        this.startUpdateTask();
    }

    @NotNull
    public String getIdentifier() {
        return this.plugin.getConfigManager().getPlaceholderPrefix();
    }

    @NotNull
    public String getAuthor() {
        return this.plugin.getDescription().getAuthors().toString();
    }

    @NotNull
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    public boolean persist() {
        return true;
    }

    public String onRequest(OfflinePlayer player, @NotNull String params) {
        Matcher matcher = this.TOPLIST_PATTERN.matcher(params);
        if (matcher.matches()) {
            int place = Integer.parseInt(matcher.group(1));
            return this.getToplistEntry(place);
        }
        if (player == null) {
            return null;
        }
        switch (params.toLowerCase()) {
            case "wins": {
                return this.getPlayerWins(player);
            }
        }
        return null;
    }

    private String getToplistEntry(int place) {
        int index = place - 1;
        String playerName = this.leaderboard.getPlayerByIndex(index);
        int wins = this.leaderboard.getWinsByIndex(index);
        if (!playerName.equals("---") && wins > 0) {
            String template = this.plugin.getConfigManager().getToplistValue();
            return template.replace("%place%", String.valueOf(place)).replace("%player%", playerName).replace("%wins%", String.valueOf(wins));
        }
        String template = this.plugin.getConfigManager().getToplistNone();
        return template.replace("%place%", String.valueOf(place));
    }

    private String getPlayerWins(OfflinePlayer player) {
        try {
            int wins = this.plugin.getDatabase().getWins(player.getUniqueId()).get();
            if (wins > 0) {
                return this.plugin.getConfigManager().getWinsValue().replace("%value%", String.valueOf(wins));
            }
            return this.plugin.getConfigManager().getWinsNone();
        } catch (Exception e) {
            return this.plugin.getConfigManager().getWinsNone();
        }
    }

    private void startUpdateTask() {
        long updateInterval = (long)this.plugin.getConfigManager().getLeaderboardUpdateSeconds() * 20L;
        this.updateTask = this.plugin.getUniversalScheduler().runTaskTimerAsynchronously(() -> this.leaderboard.updateData(null), 0L, updateInterval);
    }

    public void shutdown() {
        if (this.updateTask != null && this.updateTask instanceof MyScheduledTask) {
            ((MyScheduledTask)this.updateTask).cancel();
        }
    }

    public ActiveLeaderboard getLeaderboard() {
        return this.leaderboard;
    }

    public static class ToplistEntry {
        private final UUID uuid;
        private final String playerName;
        private final int wins;

        public ToplistEntry(UUID uuid, String playerName, int wins) {
            this.uuid = uuid;
            this.playerName = playerName;
            this.wins = wins;
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public String getPlayerName() {
            return this.playerName;
        }

        public int getWins() {
            return this.wins;
        }
    }
}

