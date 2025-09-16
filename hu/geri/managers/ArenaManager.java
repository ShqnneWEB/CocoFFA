/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.boss.BarColor
 *  org.bukkit.boss.BarFlag
 *  org.bukkit.boss.BarStyle
 *  org.bukkit.boss.BossBar
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package hu.geri.managers;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.arena.ArenaState;
import hu.geri.libs.universalScheduler.scheduling.tasks.MyScheduledTask;
import hu.geri.managers.ArenaFileManager;
import hu.geri.processor.MessageProcessor;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.StyleSetter;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArenaManager {
    private final CocoFFA plugin;
    private final ArenaFileManager arenaFileManager;
    private final Map<String, Arena> arenas;
    private final Map<UUID, String> playerArenas;
    private final Map<String, BossBar> arenaBossBars;
    private final Map<String, MyScheduledTask> countdownTasks;

    public ArenaManager(CocoFFA plugin) {
        this.plugin = plugin;
        this.arenaFileManager = new ArenaFileManager(plugin);
        this.arenas = new HashMap<String, Arena>();
        this.playerArenas = new HashMap<UUID, String>();
        this.arenaBossBars = new HashMap<String, BossBar>();
        this.countdownTasks = new HashMap<String, MyScheduledTask>();
    }

    public void loadArenas() {
        this.loadArenas(false);
    }

    public void loadArenas(boolean isReload) {
        File arenasDir = new File(this.plugin.getDataFolder(), "arenas");
        if (!arenasDir.exists()) {
            return;
        }
        File[] arenaFiles = arenasDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (arenaFiles == null) {
            return;
        }
        for (File arenaFile : arenaFiles) {
            Arena arena;
            String arenaName = arenaFile.getName().replace(".yml", "");
            if (this.arenas.containsKey(arenaName) || (arena = this.arenaFileManager.loadArena(arenaName)) == null) continue;
            this.arenas.put(arenaName, arena);
            String message = isReload ? this.plugin.getLocaleManager().colorize("&#FFEE00\u2560 &#00FF00Reloaded arena &#FFFFFF" + arenaName + "&#00FF00!") : this.plugin.getLocaleManager().colorize("&#FFEE00\u2560 &#00FF00Loaded arena &#FFFFFF" + arenaName + "&#00FF00!");
            this.plugin.getServer().getConsoleSender().sendMessage(message);
        }
    }

    public Arena createArena(String name) {
        if (this.arenas.containsKey(name)) {
            return null;
        }
        this.arenaFileManager.copyDefaultTemplate(name);
        Arena arena = this.arenaFileManager.loadArena(name);
        if (arena != null) {
            this.arenas.put(name, arena);
        }
        return arena;
    }

    public void saveArena(Arena arena) {
        this.arenaFileManager.saveArena(arena);
    }

    public Arena getArena(String name) {
        return this.arenas.get(name);
    }

    public Set<String> getArenaNames() {
        return this.arenas.keySet();
    }

    public Map<String, Arena> getArenas() {
        return this.arenas;
    }

    public String getPlayerArena(Player player) {
        return this.playerArenas.get(player.getUniqueId());
    }

    public void joinArena(Player player, Arena arena) {
        BossBar bossBar;
        if (this.playerArenas.containsKey(player.getUniqueId())) {
            return;
        }
        this.plugin.getInventoryManager().savePlayerInventory(player);
        this.applyPlayerStateManagement(player);
        this.playerArenas.put(player.getUniqueId(), arena.getName());
        arena.addPlayer(player);
        this.plugin.getWebhookManager().sendJoinWebhook(player.getName(), arena.getDisplayName(), arena.getPlayers().size(), arena.getMaxPlayers());
        String joinMessage = this.plugin.getLocaleManager().getMessage("commands.join.broadcast", "{player}", player.getName(), "{arena_name}", arena.getDisplayName(), "{current}", String.valueOf(arena.getPlayers().size()), "{max}", arena.getMaxPlayers() == -1 ? this.plugin.getConfigManager().getInfiniteSymbol() : String.valueOf(arena.getMaxPlayers()));
        for (Player arenaPlayer : arena.getPlayers()) {
            arenaPlayer.sendMessage(joinMessage);
        }
        if (arena.getStartLocation() != null && arena.getStartLocation().getWorld() != null) {
            player.teleportAsync(arena.getStartLocation());
        }
        player.getInventory().clear();
        this.plugin.getInventoryManager().giveKitToPlayer(player, arena.getName(), arena.isAutoArmorEnabled());
        player.updateInventory();
        this.plugin.getEffectManager().applyDefaultEffects(player, arena);
        if (arena.getState() == ArenaState.WAITING || arena.getState() == ArenaState.STARTED) {
            this.plugin.getScoreboardManager().showScoreboard(player, arena);
        }
        if ((bossBar = this.arenaBossBars.get(arena.getName())) != null) {
            bossBar.addPlayer(player);
        }
    }

    public void leaveArena(Player player) {
        Arena arena;
        String arenaName = this.playerArenas.remove(player.getUniqueId());
        if (arenaName != null && (arena = this.arenas.get(arenaName)) != null) {
            arena.removePlayer(player);
            this.plugin.getWebhookManager().sendLeaveWebhook(player.getName(), arena.getDisplayName(), arena.getPlayers().size(), arena.getMaxPlayers());
            String leaveMessage = this.plugin.getLocaleManager().getMessage("commands.leave.broadcast", "{player}", player.getName(), "{arena_name}", arena.getDisplayName(), "{current}", String.valueOf(arena.getPlayers().size()), "{max}", arena.getMaxPlayers() == -1 ? this.plugin.getConfigManager().getInfiniteSymbol() : String.valueOf(arena.getMaxPlayers()));
            for (Player arenaPlayer : arena.getPlayers()) {
                arenaPlayer.sendMessage(leaveMessage);
            }
            if (arena.getExitLocation() != null) {
                player.teleportAsync(arena.getExitLocation());
            }
            this.plugin.getInventoryManager().restorePlayerInventory(player, false);
            this.plugin.getScoreboardManager().removeScoreboard(player);
            this.plugin.getEffectManager().removeAllEffects(player);
            this.plugin.getBorderTeleportListener().cancelPlayerCountdown(player);
            BossBar bossBar = this.arenaBossBars.get(arenaName);
            if (bossBar != null) {
                bossBar.removePlayer(player);
            }
        }
    }

    public void startArena(Arena arena) {
        if (arena.getState() != ArenaState.STOPPED || !arena.isConfigured()) {
            return;
        }
        arena.setState(ArenaState.WAITING);
        this.plugin.getKitManager().loadKitToCache(arena.getName());
        BossBar bossBar = null;
        if (arena.isBossBarEnabled()) {
            bossBar = this.createBossBar(arena);
            this.arenaBossBars.put(arena.getName(), bossBar);
            for (Player player : arena.getPlayers()) {
                bossBar.addPlayer(player);
            }
        }
        BossBar finalBossBar = bossBar;
        arena.setStartTime(System.currentTimeMillis());
        MyScheduledTask countdownTask = this.plugin.getUniversalScheduler().runTaskTimer(() -> {
            int elapsed = (int)((System.currentTimeMillis() - arena.getStartTime()) / 1000L);
            int countdown = arena.getCountdownTime() - elapsed;
            if (countdown <= 0) {
                if (arena.getPlayers().size() < arena.getMinPlayers()) {
                    MyScheduledTask currentTask;
                    arena.setState(ArenaState.STOPPED);
                    String cancelMessage = this.plugin.getLocaleManager().getMessage("arena.countdown.cancelled", "{arena_name}", arena.getName());
                    for (Player onlinePlayer : this.plugin.getServer().getOnlinePlayers()) {
                        onlinePlayer.sendMessage(cancelMessage);
                    }
                    for (Player player : arena.getPlayers()) {
                        this.leaveArena(player);
                    }
                    this.plugin.getKitManager().clearKitCache(arena.getName());
                    if (finalBossBar != null) {
                        finalBossBar.removeAll();
                        this.arenaBossBars.remove(arena.getName());
                    }
                    if ((currentTask = this.countdownTasks.remove(arena.getName())) != null) {
                        currentTask.cancel();
                    }
                    arena.setCountdownTaskId(-1);
                    return;
                }
                arena.setState(ArenaState.STARTED);
                this.plugin.getWebhookManager().sendStartWebhook(arena.getDisplayName(), arena.getPlayers().size());
                String startMessage = this.plugin.getLocaleManager().getMessage("arena.started-broadcast", "{arena_name}", arena.getDisplayName());
                for (Player onlinePlayer : this.plugin.getServer().getOnlinePlayers()) {
                    onlinePlayer.sendMessage(startMessage);
                }
                if (finalBossBar != null) {
                    finalBossBar.removeAll();
                    this.arenaBossBars.remove(arena.getName());
                }
                this.startGameSystems(arena);
                MyScheduledTask currentTask = this.countdownTasks.remove(arena.getName());
                if (currentTask != null) {
                    currentTask.cancel();
                }
                arena.setCountdownTaskId(-1);
                return;
            }
            if (countdown <= 10 || countdown % 10 == 0) {
                String chatMessage = this.plugin.getLocaleManager().getMessage("arena.countdown.chat", "{countdown}", String.valueOf(countdown), "{arena_name}", arena.getName());
                for (Player player : arena.getPlayers()) {
                    player.sendMessage(chatMessage);
                }
            }
            if (this.plugin.getConfigManager().isBroadcastEnabled() && this.plugin.getConfigManager().getBroadcastTimes().contains(countdown)) {
                this.sendBroadcastMessage(arena, countdown);
            }
            if (arena.isActionBarEnabled()) {
                String actionBarMessage = arena.getActionBarMessage().replace("{start_seconds}", String.valueOf(countdown)).replace("{arena_name}", arena.getName()).replace("%prefix%", this.plugin.getConfigManager().getPrefix());
                actionBarMessage = MessageProcessor.process(actionBarMessage);
                for (Player player : arena.getPlayers()) {
                    player.sendActionBar(actionBarMessage);
                }
            }
            if (finalBossBar != null) {
                String bossBarMessage = arena.getBossBarTitle().replace("{start_seconds}", String.valueOf(countdown)).replace("{arena_name}", arena.getName()).replace("%prefix%", this.plugin.getConfigManager().getPrefix());
                bossBarMessage = this.plugin.getLocaleManager().colorize(bossBarMessage);
                finalBossBar.setTitle(bossBarMessage);
                finalBossBar.setProgress((double)countdown / (double)arena.getCountdownTime());
            }
        }, 0L, 20L);
        this.countdownTasks.put(arena.getName(), countdownTask);
        arena.setCountdownTaskId(1);
    }

    public void stopArena(Arena arena) {
        this.stopArena(arena, "Manual stop");
    }

    public void stopArena(Arena arena, String reason) {
        MyScheduledTask task = this.countdownTasks.remove(arena.getName());
        if (task != null) {
            task.cancel();
        }
        arena.setCountdownTaskId(-1);
        this.stopGameSystems(arena);
        this.plugin.getKitManager().clearKitCache(arena.getName());
        BossBar bossBar = this.arenaBossBars.remove(arena.getName());
        if (bossBar != null) {
            bossBar.removeAll();
        }
        for (Player player : new ArrayList<Player>(arena.getPlayers())) {
            this.leaveArena(player);
        }
        arena.resetKills();
        this.plugin.getWebhookManager().sendStopWebhook(arena.getDisplayName(), reason);
        arena.setState(ArenaState.STOPPED);
    }

    public int stopAllArenas() {
        int stoppedCount = 0;
        for (Arena arena : this.arenas.values()) {
            if (!arena.isRunning()) continue;
            ++stoppedCount;
            this.stopArena(arena, "Plugin shutdown");
        }
        return stoppedCount;
    }

    private BossBar createBossBar(Arena arena) {
        BarStyle style;
        BarColor color;
        String title = arena.getBossBarTitle().replace("{arena_name}", arena.getName()).replace("{start_seconds}", String.valueOf(arena.getCountdownTime())).replace("%prefix%", this.plugin.getConfigManager().getPrefix());
        title = this.plugin.getLocaleManager().colorize(title);
        try {
            color = BarColor.valueOf((String)arena.getBossBarColor());
        } catch (IllegalArgumentException e) {
            color = BarColor.BLUE;
        }
        try {
            style = BarStyle.valueOf((String)arena.getBossBarStyle());
        } catch (IllegalArgumentException e) {
            style = BarStyle.SOLID;
        }
        return Bukkit.createBossBar((String)title, (BarColor)color, (BarStyle)style, (BarFlag[])new BarFlag[0]);
    }

    private void sendBroadcastMessage(Arena arena, int countdown) {
        String broadcastMessage = this.plugin.getLocaleManager().getMessage("arena.countdown.broadcast", "{arena_name}", arena.getName(), "{countdown}", String.valueOf(countdown));
        broadcastMessage = MessageProcessor.process(broadcastMessage);
        String hoverMessage = this.plugin.getLocaleManager().getMessage("arena.countdown.broadcast_hover", "{arena_name}", arena.getName(), "{current}", String.valueOf(arena.getPlayers().size()), "{max}", arena.getMaxPlayers() == -1 ? this.plugin.getConfigManager().getInfiniteSymbol() : String.valueOf(arena.getMaxPlayers()));
        hoverMessage = MessageProcessor.process(hoverMessage);
        String joinButtonText = this.plugin.getLocaleManager().getMessage("arena.countdown.broadcast_join_button");
        joinButtonText = joinButtonText.replace("%prefix%", this.plugin.getConfigManager().getPrefix());
        joinButtonText = MessageProcessor.process(joinButtonText);
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (this.getPlayerArena(player) != null) continue;
            player.sendMessage(broadcastMessage);
            StyleSetter joinButton = ((TextComponent)LegacyComponentSerializer.legacySection().deserialize(joinButtonText).clickEvent(ClickEvent.runCommand("/" + this.plugin.getConfigManager().getMainCommand() + " join " + arena.getName()))).hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(hoverMessage)));
            player.sendMessage((Component)joinButton);
        }
    }

    private void startGameSystems(Arena arena) {
        boolean needsKitReapply = false;
        for (Player player : arena.getPlayers()) {
            if (!this.isInventoryEmpty(player)) continue;
            needsKitReapply = true;
            break;
        }
        if (needsKitReapply) {
            for (Player player : arena.getPlayers()) {
                if (!this.isInventoryEmpty(player)) continue;
                this.plugin.getInventoryManager().giveKitToPlayer(player, arena.getName(), arena.isAutoArmorEnabled());
            }
        }
        for (Player player : arena.getPlayers()) {
            this.plugin.getScoreboardManager().showScoreboard(player, arena);
        }
        this.plugin.getActionBarManager().startInGameActionBar(arena);
        this.plugin.getEffectManager().startEffectSystem(arena);
        this.plugin.getBorderManager().startBorderSystem(arena);
    }

    private boolean isInventoryEmpty(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType().isAir()) continue;
            return false;
        }
        return true;
    }

    private void stopGameSystems(Arena arena) {
        this.plugin.getScoreboardManager().removeAllScoreboards(arena);
        this.plugin.getActionBarManager().stopInGameActionBar(arena);
        this.plugin.getEffectManager().stopEffectSystem(arena);
        this.plugin.getBorderManager().stopBorderSystem(arena);
        this.plugin.getSpectatorManager().removeAllSpectatorsFromArena(arena.getName());
    }

    public boolean deleteArena(String arenaName) {
        Arena arena = this.arenas.get(arenaName);
        if (arena == null) {
            return false;
        }
        if (arena.isRunning()) {
            return false;
        }
        this.arenas.remove(arenaName);
        File arenaFile = new File(this.plugin.getDataFolder(), "arenas/" + arenaName + ".yml");
        boolean arenaDeleted = true;
        if (arenaFile.exists()) {
            arenaDeleted = arenaFile.delete();
        }
        this.plugin.getKitManager().deleteKit(arenaName);
        boolean kitDeleted = true;
        return arenaDeleted;
    }

    private void applyPlayerStateManagement(Player player) {
        String flyBypassPermission;
        if (!this.plugin.getConfigManager().isPlayerStateManagementEnabled()) {
            return;
        }
        String gamemodeBypassPermission = this.plugin.getConfig().getString("permissions.gamemode-bypass.permission", "cocoffa.gamemode.bypass");
        if (!player.hasPermission(gamemodeBypassPermission)) {
            List<String> allowedGamemodes = this.plugin.getConfigManager().getAllowedGamemodes();
            String currentGamemode = player.getGameMode().name();
            boolean allGamemodesAllowed = allowedGamemodes.contains("*");
            if (!allGamemodesAllowed && !allowedGamemodes.contains(currentGamemode)) {
                try {
                    String forceGamemode = this.plugin.getConfigManager().getForceGamemode();
                    GameMode targetGamemode = GameMode.valueOf((String)forceGamemode.toUpperCase());
                    player.setGameMode(targetGamemode);
                    this.plugin.getLogger().info("Changed gamemode for player " + player.getName() + " from " + currentGamemode + " to " + String.valueOf(targetGamemode));
                } catch (IllegalArgumentException e) {
                    this.plugin.getLogger().warning("Invalid gamemode in config: " + this.plugin.getConfigManager().getForceGamemode());
                    player.setGameMode(GameMode.SURVIVAL);
                }
            }
        }
        if (this.plugin.getConfigManager().isDisableFlyOnJoin() && player.isFlying() && !player.hasPermission(flyBypassPermission = this.plugin.getConfig().getString("permissions.fly-bypass.permission", "cocoffa.fly.bypass"))) {
            player.setFlying(false);
            player.setAllowFlight(false);
        }
    }
}

