/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.leaderboard;

import hu.geri.CocoFFA;
import java.util.LinkedHashMap;

public class ActiveLeaderboard {
    private LinkedHashMap<String, Integer> data = new LinkedHashMap();
    private Long lastUpdate = System.currentTimeMillis();
    private final CocoFFA plugin;

    public ActiveLeaderboard(CocoFFA plugin, Long timePeriod) {
        this.plugin = plugin;
        this.updateData(timePeriod);
    }

    public void updateData(Long timePeriod) {
        this.lastUpdate = System.currentTimeMillis();
        this.data = this.plugin.getDatabase().getTopPlayers(timePeriod == null ? null : Long.valueOf(System.currentTimeMillis() - timePeriod));
    }

    public LinkedHashMap<String, Integer> getData() {
        return this.data;
    }

    public Long getLastUpdate() {
        return this.lastUpdate;
    }

    public int getWinsByIndex(int index) {
        if (this.data.values().size() - 1 < index) {
            return 0;
        }
        return (Integer)this.data.values().toArray()[index];
    }

    public String getPlayerByIndex(int index) {
        if (this.data.keySet().size() - 1 < index) {
            return "---";
        }
        return (String)this.data.keySet().toArray()[index];
    }

    public int getPosition(String playerName) {
        int position = 1;
        for (String name : this.data.keySet()) {
            if (name.equals(playerName)) {
                return position;
            }
            ++position;
        }
        return -1;
    }

    public int getPlayerWins(String playerName) {
        return this.data.getOrDefault(playerName, 0);
    }

    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    public int size() {
        return this.data.size();
    }
}

