/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.managers;

import hu.geri.CocoFFA;
import hu.geri.libs.boostedyaml.YamlDocument;
import hu.geri.libs.boostedyaml.dvs.versioning.BasicVersioning;
import hu.geri.libs.boostedyaml.settings.dumper.DumperSettings;
import hu.geri.libs.boostedyaml.settings.general.GeneralSettings;
import hu.geri.libs.boostedyaml.settings.loader.LoaderSettings;
import hu.geri.libs.boostedyaml.settings.updater.UpdaterSettings;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigManager {
    private final CocoFFA plugin;
    private YamlDocument config;

    public ConfigManager(CocoFFA plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        try {
            this.initializeAllConfigs();
            this.config = YamlDocument.create(new File(this.plugin.getDataFolder(), "config.yml"), this.plugin.getResource("config.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).setKeepAll(true).build());
        } catch (IOException e) {
            this.plugin.getLogger().severe("Failed to load config.yml with BoostedYAML: " + e.getMessage());
        }
    }

    private void initializeAllConfigs() {
        this.plugin.saveDefaultConfig();
        this.createDirectoriesIfNeeded();
        this.createGuiConfigsIfNeeded();
        this.createWebhookConfigIfNeeded();
    }

    private void createDirectoriesIfNeeded() {
        File localeDir;
        File arenasDir;
        File guisDir = new File(this.plugin.getDataFolder(), "guis");
        if (!guisDir.exists()) {
            guisDir.mkdirs();
        }
        if (!(arenasDir = new File(this.plugin.getDataFolder(), "arenas")).exists()) {
            arenasDir.mkdirs();
        }
        if (!(localeDir = new File(this.plugin.getDataFolder(), "locale")).exists()) {
            localeDir.mkdirs();
        }
    }

    private void createGuiConfigsIfNeeded() {
        String[] guiFiles;
        for (String fileName : guiFiles = new String[]{"kit-editor-gui.yml", "main-gui.yml", "scoreboard-gui.yml", "reward-editor-gui.yml", "spectator-gui.yml"}) {
            File guiFile = new File(this.plugin.getDataFolder(), "guis/" + fileName);
            if (guiFile.exists()) continue;
            this.plugin.saveResource("guis/" + fileName, false);
        }
    }

    private void createWebhookConfigIfNeeded() {
        File webhookFile = new File(this.plugin.getDataFolder(), "webhook.yml");
        if (!webhookFile.exists()) {
            this.plugin.saveResource("webhook.yml", false);
        }
    }

    public boolean reloadConfig() {
        try {
            this.config.reload();
            return true;
        } catch (IOException e) {
            this.plugin.getLogger().severe("Failed to reload config.yml: " + e.getMessage());
            return false;
        }
    }

    public String getPrefix() {
        return this.config.getString("prefix", "&8[&6CocoFFA&8] &7\u00bb");
    }

    public String getInfiniteSymbol() {
        return this.config.getString("infiniteSymbol", "\u221e");
    }

    public String getMainCommand() {
        return this.config.getString("command.main", "ffa");
    }

    public List<String> getCommandAliases() {
        return this.config.getStringList("command.aliases");
    }

    public String getLocaleFile() {
        return this.config.getString("locale-file", "messages_hu.yml");
    }

    public String getDatabaseType() {
        return this.config.getString("settings.database.type", "sqlite");
    }

    public String getDatabaseTablePrefix() {
        return this.config.getString("settings.database.table-prefix", "cocoffa_");
    }

    public String getSQLiteFile() {
        return this.config.getString("settings.database.sqlite.file", "eotw.db");
    }

    public String getMySQLHost() {
        return this.config.getString("settings.database.mysql.host", "localhost");
    }

    public int getMySQLPort() {
        return this.config.getInt("settings.database.mysql.port", (Integer)3306);
    }

    public String getMySQLDatabase() {
        return this.config.getString("settings.database.mysql.database", "cocoffa");
    }

    public String getMySQLUsername() {
        return this.config.getString("settings.database.mysql.username", "root");
    }

    public String getMySQLPassword() {
        return this.config.getString("settings.database.mysql.password", "");
    }

    public boolean getMySQLSSL() {
        return this.config.getBoolean("settings.database.mysql.ssl", (Boolean)false);
    }

    public int getMySQLPoolSize() {
        return this.config.getInt("settings.database.mysql.pool-size", (Integer)10);
    }

    public boolean isBroadcastEnabled() {
        return this.config.getBoolean("broadcast.enabled", (Boolean)true);
    }

    public List<Integer> getBroadcastTimes() {
        return this.config.getIntList("broadcast.times");
    }

    public String getPlaceholderPrefix() {
        return this.config.getString("placeholders.prefix", "cocoffa");
    }

    public String getToplistPlaceholder() {
        return this.config.getString("placeholders.toplist_wins.placeholder", "toplist_<place>");
    }

    public String getToplistValue() {
        return this.config.getString("placeholders.toplist_wins.value", "&#f5c400#%place% &f%player% &7\u00bb &#FF3737%wins% wins");
    }

    public String getToplistNone() {
        return this.config.getString("placeholders.toplist_wins.none", "&#f5c400#%place% &f- &7\u00bb &#FF3737- wins");
    }

    public int getLeaderboardUpdateSeconds() {
        return this.config.getInt("leaderboard-update-seconds", (Integer)180);
    }

    public String getWinsPlaceholder() {
        return this.config.getString("placeholders.wins.placeholder", "wins");
    }

    public String getWinsValue() {
        return this.config.getString("placeholders.wins.value", "&f%value%");
    }

    public String getWinsNone() {
        return this.config.getString("placeholders.wins.none", "&c0");
    }

    public String getPermission(String command) {
        return this.config.getString("permissions." + command + ".permission", "cocoffa." + command);
    }

    public String getPermissionDefault(String command) {
        return this.config.getString("permissions." + command + ".default", "player");
    }

    public boolean isCommandRestrictionEnabled() {
        return this.config.getBoolean("allowed-commands.enabled", (Boolean)true);
    }

    public String getCommandBypassPermission() {
        return this.getPermission("command-bypass");
    }

    public List<String> getAllowedCommands() {
        return this.config.getStringList("allowed-commands.commands");
    }

    public boolean isEnderpearlRestrictionEnabled() {
        return this.config.getBoolean("enderpearl.restrict-outside-border", (Boolean)true);
    }

    public boolean isPlayerStateManagementEnabled() {
        return this.config.getBoolean("player-state-management.enabled", (Boolean)true);
    }

    public List<String> getAllowedGamemodes() {
        return this.config.getStringList("player-state-management.allowed-gamemodes");
    }

    public String getForceGamemode() {
        return this.config.getString("player-state-management.force-gamemode", "SURVIVAL");
    }

    public boolean isDisableFlyOnJoin() {
        return this.config.getBoolean("player-state-management.fly.disable-on-join", (Boolean)true);
    }

    public int getConfigVersion() {
        return this.config.getInt("config-version", (Integer)1);
    }

    public boolean canStaffSeeSpectators() {
        return this.config.getBoolean("spectator.staff-can-see", (Boolean)true);
    }

    public String getPlayerSelectorMaterial() {
        return this.config.getString("spectator.items.player-selector.material", "COMPASS");
    }

    public String getPlayerSelectorName() {
        return this.config.getString("spectator.items.player-selector.name", "&ePlayer Selector");
    }

    public List<String> getPlayerSelectorLore() {
        return this.config.getStringList("spectator.items.player-selector.lore");
    }

    public String getLeaveSpectateMaterial() {
        return this.config.getString("spectator.items.leave-spectate.material", "BARRIER");
    }

    public String getLeaveSpectateName() {
        return this.config.getString("spectator.items.leave-spectate.name", "&cLeave Spectate");
    }

    public List<String> getLeaveSpectateLore() {
        return this.config.getStringList("spectator.items.leave-spectate.lore");
    }

    public String getSpectatorExitGamemode() {
        return this.config.getString("spectator.exit-gamemode", "SURVIVAL");
    }

    public int getWaitingTime() {
        return this.config.getInt("waiting-time", (Integer)180);
    }

    public boolean isBorderTeleportEnabled() {
        return this.config.getBoolean("border.teleport-if-outside-border", (Boolean)true);
    }

    public int getBorderTeleportSeconds() {
        return this.config.getInt("border.seconds", (Integer)3);
    }

    public int getBorderTeleportBlocks() {
        return this.config.getInt("border.blocks", (Integer)3);
    }

    public int getMinPlayers() {
        return this.config.getInt("player-limits.min-players", (Integer)2);
    }

    public int getMaxPlayers() {
        return this.config.getInt("player-limits.max-players", (Integer)16);
    }
}

