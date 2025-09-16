/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package hu.geri;

import hu.geri.database.Database;
import hu.geri.database.impl.MySQL;
import hu.geri.database.impl.SQLite;
import hu.geri.guis.KitEditorGUI;
import hu.geri.guis.MainEditorGUI;
import hu.geri.guis.RewardEditorGUI;
import hu.geri.guis.ScoreboardEditorGUI;
import hu.geri.guis.SpectatorPlayerSelectorGUI;
import hu.geri.hook.HookManager;
import hu.geri.libs.bstats.bukkit.Metrics;
import hu.geri.libs.universalScheduler.UniversalScheduler;
import hu.geri.libs.universalScheduler.scheduling.schedulers.TaskScheduler;
import hu.geri.listeners.BorderTeleportListener;
import hu.geri.listeners.MaxPlayersPerIPListener;
import hu.geri.listeners.StarterItemListener;
import hu.geri.managers.ActionBarManager;
import hu.geri.managers.ArenaManager;
import hu.geri.managers.ArenaTickManager;
import hu.geri.managers.BorderManager;
import hu.geri.managers.ConfigManager;
import hu.geri.managers.EffectManager;
import hu.geri.managers.InventoryManager;
import hu.geri.managers.KitManager;
import hu.geri.managers.LocaleManager;
import hu.geri.managers.PlaceholderManager;
import hu.geri.managers.ScoreboardManager;
import hu.geri.managers.SpectatorItemManager;
import hu.geri.managers.SpectatorManager;
import hu.geri.managers.WebhookManager;
import hu.geri.utils.RegisterUtils;
import hu.geri.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CocoFFA
extends JavaPlugin {
    private static CocoFFA instance;
    private static TaskScheduler scheduler;
    private ConfigManager configManager;
    private LocaleManager localeManager;
    private ArenaManager arenaManager;
    private ArenaTickManager arenaTickManager;
    private Database database;
    private InventoryManager inventoryManager;
    private PlaceholderManager placeholderManager;
    private ScoreboardManager scoreboardManager;
    private EffectManager effectManager;
    private BorderManager borderManager;
    private ActionBarManager actionBarManager;
    private KitManager kitManager;
    private StarterItemListener starterItemListener;
    private KitEditorGUI kitEditorGUI;
    private MainEditorGUI mainEditorGUI;
    private ScoreboardEditorGUI scoreboardEditorGUI;
    private RewardEditorGUI rewardEditorGUI;
    private WebhookManager webhookManager;
    private HookManager hookManager;
    private MaxPlayersPerIPListener maxPlayersPerIPChecker;
    private SpectatorManager spectatorManager;
    private SpectatorItemManager spectatorItemManager;
    private SpectatorPlayerSelectorGUI spectatorPlayerSelectorGUI;
    private BorderTeleportListener borderTeleportListener;
    private UpdateChecker updateChecker;

    public void onEnable() {
        instance = this;
        scheduler = UniversalScheduler.getScheduler((Plugin)this);
        int pluginId = 27033;
        Metrics metrics = new Metrics(this, pluginId);
        this.displayBanner();
        this.configManager = new ConfigManager(this);
        this.configManager.loadConfig();
        this.localeManager = new LocaleManager(this);
        this.initializeDatabase();
        this.inventoryManager = new InventoryManager(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.effectManager = new EffectManager(this);
        this.borderManager = new BorderManager(this);
        this.actionBarManager = new ActionBarManager(this);
        this.kitManager = new KitManager(this);
        this.arenaManager = new ArenaManager(this);
        this.arenaTickManager = new ArenaTickManager(this);
        this.mainEditorGUI = new MainEditorGUI(this);
        this.scoreboardEditorGUI = new ScoreboardEditorGUI(this);
        this.rewardEditorGUI = new RewardEditorGUI(this);
        this.kitEditorGUI = new KitEditorGUI(this);
        this.webhookManager = new WebhookManager(this);
        this.hookManager = new HookManager();
        this.maxPlayersPerIPChecker = new MaxPlayersPerIPListener(this);
        this.spectatorManager = new SpectatorManager(this);
        this.spectatorItemManager = new SpectatorItemManager(this);
        this.spectatorPlayerSelectorGUI = new SpectatorPlayerSelectorGUI(this);
        this.borderTeleportListener = new BorderTeleportListener(this);
        this.starterItemListener = new StarterItemListener(this);
        this.localeManager.loadLocale();
        this.database.restoreAllInventories();
        RegisterUtils.registerCommands();
        RegisterUtils.registerEvents();
        this.setupPlaceholders();
        this.hookManager.updateHooks();
        this.arenaManager.loadArenas();
        this.arenaTickManager.start();
        scheduler.runTaskAsynchronously(() -> {
            int spigotResourceId = 125913;
            this.updateChecker = new UpdateChecker(this, spigotResourceId);
            this.updateChecker.init();
            scheduler.runTask(() -> this.getServer().getPluginManager().registerEvents(new Listener(){

                @EventHandler
                public void onJoin(PlayerJoinEvent event) {
                    Player player = event.getPlayer();
                    if (player.hasPermission("cocoffa.admin.update-notify")) {
                        scheduler.runTaskLaterAsynchronously(() -> {
                            if (CocoFFA.this.updateChecker != null) {
                                CocoFFA.this.updateChecker.notifyIfUpdateAvailable(player);
                            }
                        }, 40L);
                    }
                }
            }, (Plugin)this));
        });
    }

    private void initializeDatabase() {
        String type = this.configManager.getDatabaseType();
        String tablePrefix = this.configManager.getDatabaseTablePrefix();
        if (type.equalsIgnoreCase("mysql")) {
            String host = this.configManager.getMySQLHost();
            int port = this.configManager.getMySQLPort();
            String database = this.configManager.getMySQLDatabase();
            String username = this.configManager.getMySQLUsername();
            String password = this.configManager.getMySQLPassword();
            boolean ssl = this.configManager.getMySQLSSL();
            int poolSize = this.configManager.getMySQLPoolSize();
            this.database = new MySQL(this, host, port, database, username, password, ssl, poolSize, tablePrefix);
        } else {
            String sqliteFile = this.configManager.getSQLiteFile();
            this.database = new SQLite(this, tablePrefix, sqliteFile);
        }
        try {
            this.database.initialize().join();
            this.getLogger().info("Database initialized successfully");
        } catch (Exception e) {
            this.getLogger().severe("Failed to initialize database: " + e.getMessage());
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
        }
    }

    public void onDisable() {
        if (this.arenaTickManager != null) {
            this.arenaTickManager.stop();
        }
        if (this.arenaManager != null) {
            this.arenaManager.stopAllArenas();
        }
        if (this.borderTeleportListener != null) {
            this.borderTeleportListener.cleanup();
        }
        if (this.placeholderManager != null) {
            this.placeholderManager.shutdown();
        }
        if (this.database != null) {
            this.database.shutdown();
        }
        this.getLogger().info("Disabling " + this.getDescription().getName() + " v" + this.getDescription().getVersion());
    }

    private void setupPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.placeholderManager = new PlaceholderManager(this);
            this.placeholderManager.register();
            this.getLogger().info("PlaceholderAPI integration enabled!");
        } else {
            this.getLogger().info("PlaceholderAPI not found, placeholder integration disabled.");
        }
    }

    public static CocoFFA getInstance() {
        return instance;
    }

    public static TaskScheduler getScheduler() {
        return scheduler;
    }

    public TaskScheduler getUniversalScheduler() {
        return scheduler;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public LocaleManager getLocaleManager() {
        return this.localeManager;
    }

    public ArenaManager getArenaManager() {
        return this.arenaManager;
    }

    public Database getDatabase() {
        return this.database;
    }

    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public PlaceholderManager getPlaceholderManager() {
        return this.placeholderManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return this.scoreboardManager;
    }

    public EffectManager getEffectManager() {
        return this.effectManager;
    }

    public BorderManager getBorderManager() {
        return this.borderManager;
    }

    public ActionBarManager getActionBarManager() {
        return this.actionBarManager;
    }

    public KitManager getKitManager() {
        return this.kitManager;
    }

    public StarterItemListener getStarterItemListener() {
        return this.starterItemListener;
    }

    public RewardEditorGUI getRewardEditorGUI() {
        return this.rewardEditorGUI;
    }

    public WebhookManager getWebhookManager() {
        return this.webhookManager;
    }

    public MainEditorGUI getMainEditorGUI() {
        return this.mainEditorGUI;
    }

    public ScoreboardEditorGUI getScoreboardEditorGUI() {
        return this.scoreboardEditorGUI;
    }

    public KitEditorGUI getKitEditorGUI() {
        return this.kitEditorGUI;
    }

    public HookManager getHookManager() {
        return this.hookManager;
    }

    public SpectatorManager getSpectatorManager() {
        return this.spectatorManager;
    }

    public SpectatorItemManager getSpectatorItemManager() {
        return this.spectatorItemManager;
    }

    public SpectatorPlayerSelectorGUI getSpectatorPlayerSelectorGUI() {
        return this.spectatorPlayerSelectorGUI;
    }

    public BorderTeleportListener getBorderTeleportListener() {
        return this.borderTeleportListener;
    }

    public MaxPlayersPerIPListener getMaxPlayersPerIPChecker() {
        return this.maxPlayersPerIPChecker;
    }

    public ArenaTickManager getArenaTickManager() {
        return this.arenaTickManager;
    }

    public UpdateChecker getUpdateChecker() {
        return this.updateChecker;
    }

    private void displayBanner() {
        String serverName = this.getServer().getName();
        String serverVersion = this.getServer().getVersion();
        System.out.println("");
        System.out.println("\u001b[38;5;51m   _____                ______ ______      \u001b[0m");
        System.out.println("\u001b[38;5;51m  / ____|              |  ____|  ____/\\    \u001b[0m");
        System.out.println("\u001b[38;5;51m | |     ___   ___ ___ | |__  | |__ /  \\   \u001b[0m");
        System.out.println("\u001b[38;5;51m | |    / _ \\ / __/ _ \\|  __| |  __/ /\\ \\  \u001b[0m");
        System.out.println("\u001b[38;5;51m | |___| (_) | (_| (_) | |    | | / ____ \\ \u001b[0m");
        System.out.println("\u001b[38;5;51m  \\_____\\___/ \\___\\___/|_|    |_|/_/    \\_\\\u001b[0m");
        System.out.println("");
        System.out.println("\u001b[38;5;51m   The plugin successfully started.\u001b[0m");
        System.out.println("\u001b[38;5;45m   CocoFFA " + serverName + " " + serverVersion + "\u001b[0m");
        System.out.println("\u001b[38;5;39m   Discord @ dc.cocostudios.com\u001b[0m");
        System.out.println("");
    }
}

