/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package hu.geri.managers;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.arena.BorderPhase;
import hu.geri.arena.EffectPhase;
import hu.geri.utils.LocationSerializer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class ArenaFileManager {
    private final CocoFFA plugin;

    public ArenaFileManager(CocoFFA plugin) {
        this.plugin = plugin;
    }

    public File[] getArenaFiles() {
        File arenasDir = new File(this.plugin.getDataFolder(), "arenas");
        if (!arenasDir.exists()) {
            arenasDir.mkdirs();
            return new File[0];
        }
        File[] arenaFiles = arenasDir.listFiles((dir, name) -> name.endsWith(".yml"));
        return arenaFiles != null ? arenaFiles : new File[]{};
    }

    public Arena loadArena(String arenaName) {
        int startTime;
        ConfigurationSection phaseSection;
        File arenaFile = new File(this.plugin.getDataFolder(), "arenas/" + arenaName + ".yml");
        if (!arenaFile.exists()) {
            return null;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration((File)arenaFile);
        Arena arena = new Arena(arenaName);
        if (config.contains("enabled")) {
            arena.setEnabled(config.getBoolean("enabled"));
        }
        if (config.contains("displayName")) {
            arena.setDisplayName(config.getString("displayName"));
        }
        if (config.contains("startLocation")) {
            arena.setStartLocationString(config.getString("startLocation"));
        }
        if (config.contains("exitLocation")) {
            arena.setExitLocationString(config.getString("exitLocation"));
        }
        if (config.contains("borderCenter")) {
            arena.setBorderCenterString(config.getString("borderCenter"));
        }
        if (config.contains("countdown.actionbar.enabled")) {
            arena.setActionBarEnabled(config.getBoolean("countdown.actionbar.enabled"));
        }
        if (config.contains("countdown.actionbar.message")) {
            arena.setActionBarMessage(config.getString("countdown.actionbar.message"));
        }
        if (config.contains("countdown.bossbar.enabled")) {
            arena.setBossBarEnabled(config.getBoolean("countdown.bossbar.enabled"));
        }
        if (config.contains("countdown.bossbar.color")) {
            arena.setBossBarColor(config.getString("countdown.bossbar.color"));
        }
        if (config.contains("countdown.bossbar.style")) {
            arena.setBossBarStyle(config.getString("countdown.bossbar.style"));
        }
        if (config.contains("countdown.bossbar.title")) {
            arena.setBossBarTitle(config.getString("countdown.bossbar.title"));
        }
        if (config.contains("scoreboard.enabled")) {
            arena.setScoreboardEnabled(config.getBoolean("scoreboard.enabled"));
        }
        if (config.contains("scoreboard.title")) {
            arena.setScoreboardTitle(config.getString("scoreboard.title"));
        }
        if (config.contains("scoreboard.lines")) {
            arena.setScoreboardLines(config.getStringList("scoreboard.lines"));
        }
        if (config.contains("ingame-actionbar.enabled")) {
            arena.setInGameActionBarEnabled(config.getBoolean("ingame-actionbar.enabled"));
        }
        if (config.contains("ingame-actionbar.message")) {
            arena.setInGameActionBarMessage(config.getString("ingame-actionbar.message"));
        }
        if (config.contains("effects.defaults")) {
            arena.setDefaultEffects(config.getStringList("effects.defaults"));
        }
        if (config.contains("effects")) {
            HashMap<Integer, EffectPhase> effectPhases = new HashMap<Integer, EffectPhase>();
            ConfigurationSection effectsSection = config.getConfigurationSection("effects");
            if (effectsSection != null) {
                for (String key : effectsSection.getKeys(false)) {
                    if (key.equals("defaults")) continue;
                    try {
                        int phaseNumber = Integer.parseInt(key);
                        phaseSection = effectsSection.getConfigurationSection(key);
                        if (phaseSection == null) continue;
                        startTime = phaseSection.getInt("start", 0);
                        List effects = phaseSection.getStringList("effects");
                        effectPhases.put(phaseNumber, new EffectPhase(startTime, effects));
                    } catch (NumberFormatException phaseNumber) {}
                }
            }
            arena.setEffectPhases(effectPhases);
        }
        if (config.contains("border.default")) {
            arena.setDefaultBorderSize(config.getInt("border.default"));
        }
        if (config.contains("border")) {
            HashMap<Integer, BorderPhase> borderPhases = new HashMap<Integer, BorderPhase>();
            ConfigurationSection borderSection = config.getConfigurationSection("border");
            if (borderSection != null) {
                for (String key : borderSection.getKeys(false)) {
                    if (key.equals("default")) continue;
                    try {
                        int phaseNumber = Integer.parseInt(key);
                        phaseSection = borderSection.getConfigurationSection(key);
                        if (phaseSection == null) continue;
                        startTime = phaseSection.getInt("start", 0);
                        int size = phaseSection.getInt("size", 100);
                        int seconds = phaseSection.getInt("seconds", 5);
                        borderPhases.put(phaseNumber, new BorderPhase(startTime, size, seconds));
                    } catch (NumberFormatException numberFormatException) {}
                }
            }
            arena.setBorderPhases(borderPhases);
        }
        if (config.contains("starter.enabled")) {
            arena.setStarterEnabled(config.getBoolean("starter.enabled"));
        }
        if (config.contains("starter.material")) {
            arena.setStarterMaterial(config.getString("starter.material"));
        }
        if (config.contains("starter.glow")) {
            arena.setStarterGlow(config.getBoolean("starter.glow"));
        }
        if (config.contains("starter.name")) {
            arena.setStarterName(config.getString("starter.name"));
        }
        if (config.contains("starter.lore")) {
            arena.setStarterLore(config.getStringList("starter.lore"));
        }
        return arena;
    }

    public void saveArena(Arena arena) {
        String path;
        Object phase;
        File arenaFile = new File(this.plugin.getDataFolder(), "arenas/" + arena.getName() + ".yml");
        arenaFile.getParentFile().mkdirs();
        YamlConfiguration config = arenaFile.exists() ? YamlConfiguration.loadConfiguration((File)arenaFile) : new YamlConfiguration();
        config.set("displayName", (Object)arena.getDisplayName());
        config.set("startLocation", (Object)(arena.getStartLocation() != null ? LocationSerializer.serialize(arena.getStartLocation()) : ""));
        config.set("exitLocation", (Object)(arena.getExitLocation() != null ? LocationSerializer.serialize(arena.getExitLocation()) : ""));
        config.set("borderCenter", (Object)(arena.getBorderCenterLocation() != null ? LocationSerializer.serialize(arena.getBorderCenterLocation()) : ""));
        config.set("countdown.actionbar.enabled", (Object)arena.isActionBarEnabled());
        config.set("countdown.actionbar.message", (Object)arena.getActionBarMessage());
        config.set("countdown.bossbar.enabled", (Object)arena.isBossBarEnabled());
        config.set("countdown.bossbar.color", (Object)arena.getBossBarColor());
        config.set("countdown.bossbar.style", (Object)arena.getBossBarStyle());
        config.set("countdown.bossbar.title", (Object)arena.getBossBarTitle());
        config.set("scoreboard.enabled", (Object)arena.isScoreboardEnabled());
        config.set("scoreboard.title", (Object)arena.getScoreboardTitle());
        config.set("scoreboard.lines", arena.getScoreboardLines());
        config.set("ingame-actionbar.enabled", (Object)arena.isInGameActionBarEnabled());
        config.set("ingame-actionbar.message", (Object)arena.getInGameActionBarMessage());
        config.set("effects.defaults", arena.getDefaultEffects());
        for (Map.Entry<Integer, EffectPhase> entry : arena.getEffectPhases().entrySet()) {
            phase = entry.getValue();
            path = "effects." + String.valueOf(entry.getKey());
            config.set(path + ".start", (Object)((EffectPhase)phase).getStartTime());
            config.set(path + ".effects", ((EffectPhase)phase).getEffects());
        }
        config.set("border.default", (Object)arena.getDefaultBorderSize());
        for (Map.Entry<Integer, Object> entry : arena.getBorderPhases().entrySet()) {
            phase = (BorderPhase)entry.getValue();
            path = "border." + String.valueOf(entry.getKey());
            config.set(path + ".start", (Object)((BorderPhase)phase).getStartTime());
            config.set(path + ".size", (Object)((BorderPhase)phase).getSize());
            config.set(path + ".seconds", (Object)((BorderPhase)phase).getSeconds());
        }
        config.set("starter.enabled", (Object)arena.isStarterEnabled());
        config.set("starter.material", (Object)arena.getStarterMaterial());
        config.set("starter.glow", (Object)arena.isStarterGlow());
        config.set("starter.name", (Object)arena.getStarterName());
        config.set("starter.lore", arena.getStarterLore());
        try {
            config.save(arenaFile);
        } catch (IOException e) {
            this.plugin.getLogger().severe("Could not save arena " + arena.getName() + ": " + e.getMessage());
        }
    }

    public void copyDefaultTemplate(String arenaName) {
        File targetFile = new File(this.plugin.getDataFolder(), "arenas/" + arenaName + ".yml");
        targetFile.getParentFile().mkdirs();
        try (InputStream in = this.plugin.getResource("arenas/default.yml");){
            if (in == null) {
                throw new RuntimeException("Critical error: default.yml resource not found in JAR!");
            }
            Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            YamlConfiguration config = YamlConfiguration.loadConfiguration((File)targetFile);
            config.set("displayName", (Object)arenaName);
            config.save(targetFile);
        } catch (Exception e) {
            this.plugin.getLogger().severe("Failed to copy default template for arena " + arenaName + ": " + e.getMessage());
            throw new RuntimeException("Failed to create arena from template", e);
        }
    }
}

