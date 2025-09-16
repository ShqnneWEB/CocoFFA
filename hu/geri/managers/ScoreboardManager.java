/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package hu.geri.managers;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.hook.PlaceHolderAPIHook;
import hu.geri.libs.fastboard.FastBoard;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;

public class ScoreboardManager {
    private final CocoFFA plugin;
    private final Map<Player, FastBoard> playerBoards;

    public ScoreboardManager(CocoFFA plugin) {
        this.plugin = plugin;
        this.playerBoards = new HashMap<Player, FastBoard>();
    }

    public void showScoreboard(Player player, Arena arena) {
        if (!arena.isScoreboardEnabled()) {
            return;
        }
        FastBoard board = new FastBoard(player);
        String title = this.processPlaceholders(player, arena.getScoreboardTitle().replace("{arena_name}", arena.getName()));
        board.updateTitle(title);
        this.updateScoreboard(player, arena, board);
        this.playerBoards.put(player, board);
    }

    public void updateScoreboard(Player player, Arena arena, FastBoard board) {
        int alive = arena.getPlayers().size();
        int kills = arena.getPlayerKills(player);
        List<String> lines = arena.getScoreboardLines();
        String[] processedLines = new String[lines.size()];
        for (int i = 0; i < lines.size(); ++i) {
            String line = lines.get(i);
            String processedLine = line.replace("{alive_players}", String.valueOf(alive)).replace("{kills}", String.valueOf(kills)).replace("{arena_name}", arena.getName());
            processedLines[i] = this.processPlaceholders(player, processedLine);
        }
        board.updateLines(processedLines);
    }

    public void updateAllScoreboards(Arena arena) {
        for (Player player : arena.getPlayers()) {
            FastBoard board = this.playerBoards.get(player);
            if (board == null) continue;
            String title = this.processPlaceholders(player, arena.getScoreboardTitle().replace("{arena_name}", arena.getName()));
            board.updateTitle(title);
            this.updateScoreboard(player, arena, board);
        }
    }

    public void removeScoreboard(Player player) {
        FastBoard board = this.playerBoards.remove(player);
        if (board != null) {
            board.delete();
        }
    }

    public void removeAllScoreboards(Arena arena) {
        for (Player player : arena.getPlayers()) {
            this.removeScoreboard(player);
        }
    }

    private String processPlaceholders(Player player, String text) {
        String processed = PlaceHolderAPIHook.parsePlaceholders(player, text);
        return this.plugin.getLocaleManager().colorize(processed);
    }
}

