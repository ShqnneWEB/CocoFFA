/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 */
package hu.geri.managers;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.arena.ArenaState;
import hu.geri.libs.universalScheduler.scheduling.tasks.MyScheduledTask;
import java.io.File;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ArenaTickManager {
    private final CocoFFA plugin;
    private MyScheduledTask tickTask;

    public ArenaTickManager(CocoFFA plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (this.tickTask != null && !this.tickTask.isCancelled()) {
            this.tickTask.cancel();
        }
        this.tickTask = this.plugin.getUniversalScheduler().runTaskTimer(() -> {
            try {
                for (Arena arena : this.plugin.getArenaManager().getArenas().values()) {
                    if (arena.getState() == ArenaState.STOPPED) continue;
                    this.checkArena(arena);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }, 0L, 20L);
    }

    public void stop() {
        if (this.tickTask != null && !this.tickTask.isCancelled()) {
            this.tickTask.cancel();
            this.tickTask = null;
        }
    }

    public void restart() {
        this.stop();
        this.start();
    }

    private void checkArena(Arena arena) {
        if (arena.getState() == ArenaState.STARTED && arena.getPlayers().size() <= 1) {
            if (arena.getPlayers().size() == 1) {
                Player winner = arena.getPlayers().iterator().next();
                String winnerBroadcast = this.plugin.getLocaleManager().getMessage("arena.winner-broadcast", "{player}", winner.getName(), "{arena_name}", arena.getDisplayName());
                for (Player onlinePlayer : this.plugin.getServer().getOnlinePlayers()) {
                    onlinePlayer.sendMessage(winnerBroadcast);
                }
                this.plugin.getDatabase().addWin(winner.getUniqueId(), winner.getName());
                this.plugin.getWebhookManager().sendWinWebhook(winner.getName(), arena.getDisplayName(), arena.getPlayerKills(winner));
                String winnerName = winner.getName();
                String arenaName = arena.getName();
                this.plugin.getUniversalScheduler().runTaskLater(() -> {
                    Player winnerPlayer = this.plugin.getServer().getPlayerExact(winnerName);
                    if (winnerPlayer != null && winnerPlayer.isOnline()) {
                        Arena tempArena = new Arena(arenaName);
                        tempArena.setDisplayName(arena.getDisplayName());
                        this.executeRewardCommands(winnerPlayer, tempArena);
                    }
                }, 40L);
            }
            this.plugin.getArenaManager().stopArena(arena);
            return;
        }
        if (arena.isScoreboardEnabled()) {
            this.plugin.getScoreboardManager().updateAllScoreboards(arena);
        }
        if (arena.getState() == ArenaState.STARTED && arena.isInGameActionBarEnabled()) {
            for (Player player : arena.getPlayers()) {
                this.plugin.getActionBarManager().updateSingleActionBar(player, arena);
            }
        }
    }

    private void executeRewardCommands(Player winner, Arena arena) {
        File rewardsFile = new File(this.plugin.getDataFolder(), "rewards.yml");
        if (!rewardsFile.exists()) {
            return;
        }
        YamlConfiguration rewardsConfig = YamlConfiguration.loadConfiguration((File)rewardsFile);
        List commands = rewardsConfig.getStringList("arenas." + arena.getName() + ".commands");
        for (String command : commands) {
            if ((command = command.replace("{player}", winner.getName()).replace("{arena_name}", arena.getName()).replace("{display_name}", arena.getDisplayName())).startsWith("[PLAYER]")) {
                command = command.substring(8).trim();
                winner.performCommand(command);
                continue;
            }
            this.plugin.getServer().dispatchCommand((CommandSender)this.plugin.getServer().getConsoleSender(), command);
        }
    }
}

