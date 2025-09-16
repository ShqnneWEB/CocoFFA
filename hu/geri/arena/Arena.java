/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package hu.geri.arena;

import hu.geri.CocoFFA;
import hu.geri.arena.ArenaState;
import hu.geri.arena.BorderPhase;
import hu.geri.arena.EffectPhase;
import hu.geri.managers.ArenaFileManager;
import hu.geri.utils.LocationSerializer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Arena {
    private final String name;
    private String displayName;
    private Location startLocation;
    private Location exitLocation;
    private Location borderCenterLocation;
    private String startLocationString;
    private String exitLocationString;
    private String borderCenterString;
    private ItemStack[] kit;
    private final Set<Player> players;
    private ArenaState state;
    private int countdownTaskId;
    private long startTime;
    private Player lastWinner;
    private boolean enabled;
    private boolean actionBarEnabled;
    private String actionBarMessage;
    private boolean bossBarEnabled;
    private String bossBarColor;
    private String bossBarStyle;
    private String bossBarTitle;
    private boolean scoreboardEnabled;
    private String scoreboardTitle;
    private List<String> scoreboardLines;
    private boolean inGameActionBarEnabled;
    private String inGameActionBarMessage;
    private final Map<Player, Integer> playerKills;
    private List<String> defaultEffects;
    private Map<Integer, EffectPhase> effectPhases;
    private int defaultBorderSize;
    private Map<Integer, BorderPhase> borderPhases;
    private boolean starterEnabled;
    private String starterMaterial;
    private boolean starterGlow;
    private String starterName;
    private List<String> starterLore;
    private boolean autoArmorEnabled;

    public Arena(String name) {
        this.name = name;
        this.displayName = name;
        this.players = new HashSet<Player>();
        this.state = ArenaState.STOPPED;
        this.countdownTaskId = -1;
        this.enabled = true;
        this.actionBarEnabled = true;
        this.actionBarMessage = "%prefix% &e{start_seconds} &7seconds until &6{arena_name} &7starts!";
        this.bossBarEnabled = true;
        this.bossBarColor = "BLUE";
        this.bossBarStyle = "SOLID";
        this.bossBarTitle = "%prefix% &e{start_seconds} &7seconds until &6{arena_name} &7starts!";
        this.scoreboardEnabled = true;
        this.scoreboardTitle = "&6{arena_name} &7FFA";
        this.scoreboardLines = Arrays.asList("", "&7Information:", "&fRound: %round%", "&fPlayers left: %alivePlayers%", "", "&7Event:", "&f%state%: %time_formatted%", "", "&fplayyourserver.com", "");
        this.inGameActionBarEnabled = true;
        this.inGameActionBarMessage = "&7Alive: &e{alive_players} &7| &7Kills: &e{kills}";
        this.playerKills = new HashMap<Player, Integer>();
        this.defaultEffects = new ArrayList<String>();
        this.effectPhases = new HashMap<Integer, EffectPhase>();
        this.defaultBorderSize = 100;
        this.borderPhases = new HashMap<Integer, BorderPhase>();
        this.starterEnabled = true;
        this.starterMaterial = "BLAZE_POWDER";
        this.starterGlow = true;
        this.starterName = "&#FFEE00%displayName% Sumo Starter";
        this.starterLore = Arrays.asList(" ", " &7- &fStarts an arena that anyone can join!", " ", "&#FFFF00\u1d04\u029f\u026a\u1d04\u1d0b \u1d1b\u1d0f s\u1d1b\u1d00\u0280\u1d1b");
        this.autoArmorEnabled = true;
    }

    public String getName() {
        return this.name;
    }

    public Location getStartLocation() {
        if (this.startLocationString != null) {
            return LocationSerializer.deserialize(this.startLocationString);
        }
        return this.startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
        this.startLocationString = LocationSerializer.serialize(startLocation);
    }

    public Location getExitLocation() {
        if (this.exitLocationString != null) {
            return LocationSerializer.deserialize(this.exitLocationString);
        }
        return this.exitLocation;
    }

    public void setExitLocation(Location exitLocation) {
        this.exitLocation = exitLocation;
        this.exitLocationString = LocationSerializer.serialize(exitLocation);
    }

    public Location getBorderCenterLocation() {
        if (this.borderCenterString != null) {
            return LocationSerializer.deserialize(this.borderCenterString);
        }
        return this.borderCenterLocation;
    }

    public void setBorderCenter(Location borderCenter) {
        this.borderCenterLocation = borderCenter;
        this.borderCenterString = LocationSerializer.serialize(borderCenter);
    }

    public double getCurrentBorderSize() {
        Location borderCenter = this.getBorderCenterLocation();
        if (borderCenter == null || borderCenter.getWorld() == null) {
            return this.defaultBorderSize;
        }
        return borderCenter.getWorld().getWorldBorder().getSize();
    }

    public ItemStack[] getKit() {
        return this.kit;
    }

    public void setKit(ItemStack[] kit) {
        this.kit = kit;
    }

    public Set<Player> getPlayers() {
        return this.players;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public ArenaState getState() {
        return this.state;
    }

    public void setState(ArenaState state) {
        this.state = state;
    }

    public int getCountdownTime() {
        return CocoFFA.getInstance().getConfigManager().getWaitingTime();
    }

    public int getCountdownTaskId() {
        return this.countdownTaskId;
    }

    public void setCountdownTaskId(int countdownTaskId) {
        this.countdownTaskId = countdownTaskId;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public boolean isConfigured() {
        return this.startLocationString != null && !this.startLocationString.trim().isEmpty() && this.exitLocationString != null && !this.exitLocationString.trim().isEmpty() && this.borderCenterString != null && !this.borderCenterString.trim().isEmpty();
    }

    public List<String> getMissingSettings() {
        ArrayList<String> missing = new ArrayList<String>();
        if (this.startLocationString == null || this.startLocationString.trim().isEmpty()) {
            missing.add("Start Location is missing");
        }
        if (this.exitLocationString == null || this.exitLocationString.trim().isEmpty()) {
            missing.add("Exit Location is missing");
        }
        if (this.borderCenterString == null || this.borderCenterString.trim().isEmpty()) {
            missing.add("Border Center Location is missing");
        }
        return missing;
    }

    public boolean isRunning() {
        return this.state == ArenaState.WAITING || this.state == ArenaState.STARTED;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMinPlayers() {
        int minPlayers = CocoFFA.getInstance().getConfigManager().getMinPlayers();
        return Math.max(2, minPlayers);
    }

    public int getMaxPlayers() {
        return CocoFFA.getInstance().getConfigManager().getMaxPlayers();
    }

    public boolean isActionBarEnabled() {
        return this.actionBarEnabled;
    }

    public void setActionBarEnabled(boolean actionBarEnabled) {
        this.actionBarEnabled = actionBarEnabled;
    }

    public String getActionBarMessage() {
        return this.actionBarMessage;
    }

    public void setActionBarMessage(String actionBarMessage) {
        this.actionBarMessage = actionBarMessage;
    }

    public boolean isBossBarEnabled() {
        return this.bossBarEnabled;
    }

    public void setBossBarEnabled(boolean bossBarEnabled) {
        this.bossBarEnabled = bossBarEnabled;
    }

    public String getBossBarColor() {
        return this.bossBarColor;
    }

    public void setBossBarColor(String bossBarColor) {
        this.bossBarColor = bossBarColor;
    }

    public String getBossBarStyle() {
        return this.bossBarStyle;
    }

    public void setBossBarStyle(String bossBarStyle) {
        this.bossBarStyle = bossBarStyle;
    }

    public String getBossBarTitle() {
        return this.bossBarTitle;
    }

    public void setBossBarTitle(String bossBarTitle) {
        this.bossBarTitle = bossBarTitle;
    }

    public boolean isScoreboardEnabled() {
        return this.scoreboardEnabled;
    }

    public void setScoreboardEnabled(boolean scoreboardEnabled) {
        this.scoreboardEnabled = scoreboardEnabled;
    }

    public String getScoreboardTitle() {
        return this.scoreboardTitle;
    }

    public void setScoreboardTitle(String scoreboardTitle) {
        this.scoreboardTitle = scoreboardTitle;
    }

    public List<String> getScoreboardLines() {
        return this.scoreboardLines;
    }

    public void setScoreboardLines(List<String> scoreboardLines) {
        this.scoreboardLines = scoreboardLines;
    }

    public boolean isInGameActionBarEnabled() {
        return this.inGameActionBarEnabled;
    }

    public void setInGameActionBarEnabled(boolean inGameActionBarEnabled) {
        this.inGameActionBarEnabled = inGameActionBarEnabled;
    }

    public String getInGameActionBarMessage() {
        return this.inGameActionBarMessage;
    }

    public void setInGameActionBarMessage(String inGameActionBarMessage) {
        this.inGameActionBarMessage = inGameActionBarMessage;
    }

    public Map<Player, Integer> getPlayerKills() {
        return this.playerKills;
    }

    public int getPlayerKills(Player player) {
        return this.playerKills.getOrDefault(player, 0);
    }

    public void addKill(Player player) {
        this.playerKills.put(player, this.getPlayerKills(player) + 1);
    }

    public void resetKills() {
        this.playerKills.clear();
    }

    public List<String> getDefaultEffects() {
        return this.defaultEffects;
    }

    public void setDefaultEffects(List<String> defaultEffects) {
        this.defaultEffects = defaultEffects;
    }

    public Map<Integer, EffectPhase> getEffectPhases() {
        return this.effectPhases;
    }

    public void setEffectPhases(Map<Integer, EffectPhase> effectPhases) {
        this.effectPhases = effectPhases;
    }

    public int getDefaultBorderSize() {
        return this.defaultBorderSize;
    }

    public void setDefaultBorderSize(int defaultBorderSize) {
        this.defaultBorderSize = defaultBorderSize;
    }

    public Map<Integer, BorderPhase> getBorderPhases() {
        return this.borderPhases;
    }

    public void setBorderPhases(Map<Integer, BorderPhase> borderPhases) {
        this.borderPhases = borderPhases;
    }

    public boolean isStarterEnabled() {
        return this.starterEnabled;
    }

    public void setStarterEnabled(boolean starterEnabled) {
        this.starterEnabled = starterEnabled;
    }

    public String getStarterMaterial() {
        return this.starterMaterial;
    }

    public void setStarterMaterial(String starterMaterial) {
        this.starterMaterial = starterMaterial;
    }

    public boolean isStarterGlow() {
        return this.starterGlow;
    }

    public void setStarterGlow(boolean starterGlow) {
        this.starterGlow = starterGlow;
    }

    public String getStarterName() {
        return this.starterName;
    }

    public void setStarterName(String starterName) {
        this.starterName = starterName;
    }

    public List<String> getStarterLore() {
        return this.starterLore;
    }

    public void setStarterLore(List<String> starterLore) {
        this.starterLore = starterLore;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setBorderCenterLocation(Location borderCenterLocation) {
        this.borderCenterLocation = borderCenterLocation;
        this.borderCenterString = LocationSerializer.serialize(borderCenterLocation);
    }

    public boolean isAutoArmorEnabled() {
        return this.autoArmorEnabled;
    }

    public void setAutoArmorEnabled(boolean autoArmorEnabled) {
        this.autoArmorEnabled = autoArmorEnabled;
    }

    public Player getLastWinner() {
        return this.lastWinner;
    }

    public void setLastWinner(Player winner) {
        this.lastWinner = winner;
    }

    public void setStartLocationString(String startLocationString) {
        this.startLocationString = startLocationString;
    }

    public void setExitLocationString(String exitLocationString) {
        this.exitLocationString = exitLocationString;
    }

    public void setBorderCenterString(String borderCenterString) {
        this.borderCenterString = borderCenterString;
    }

    public boolean reload() {
        try {
            CocoFFA plugin = CocoFFA.getInstance();
            ArenaFileManager arenaFileManager = new ArenaFileManager(plugin);
            Arena reloadedArena = arenaFileManager.loadArena(this.name);
            if (reloadedArena == null) {
                return false;
            }
            this.displayName = reloadedArena.displayName;
            this.enabled = reloadedArena.enabled;
            this.actionBarEnabled = reloadedArena.actionBarEnabled;
            this.actionBarMessage = reloadedArena.actionBarMessage;
            this.bossBarEnabled = reloadedArena.bossBarEnabled;
            this.bossBarColor = reloadedArena.bossBarColor;
            this.bossBarStyle = reloadedArena.bossBarStyle;
            this.bossBarTitle = reloadedArena.bossBarTitle;
            this.scoreboardEnabled = reloadedArena.scoreboardEnabled;
            this.scoreboardTitle = reloadedArena.scoreboardTitle;
            this.scoreboardLines = reloadedArena.scoreboardLines;
            this.inGameActionBarEnabled = reloadedArena.inGameActionBarEnabled;
            this.inGameActionBarMessage = reloadedArena.inGameActionBarMessage;
            this.defaultEffects = reloadedArena.defaultEffects;
            this.effectPhases = reloadedArena.effectPhases;
            this.defaultBorderSize = reloadedArena.defaultBorderSize;
            this.borderPhases = reloadedArena.borderPhases;
            this.starterEnabled = reloadedArena.starterEnabled;
            this.starterMaterial = reloadedArena.starterMaterial;
            this.starterGlow = reloadedArena.starterGlow;
            this.starterName = reloadedArena.starterName;
            this.starterLore = reloadedArena.starterLore;
            this.autoArmorEnabled = reloadedArena.autoArmorEnabled;
            if (!this.isRunning()) {
                this.startLocationString = reloadedArena.startLocationString;
                this.exitLocationString = reloadedArena.exitLocationString;
                this.borderCenterString = reloadedArena.borderCenterString;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

