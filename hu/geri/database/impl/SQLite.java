/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.zaxxer.hikari.HikariConfig
 *  com.zaxxer.hikari.HikariDataSource
 */
package hu.geri.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hu.geri.CocoFFA;
import hu.geri.database.Database;
import hu.geri.managers.PlaceholderManager;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SQLite
implements Database {
    private final CocoFFA plugin;
    private HikariDataSource dataSource;
    private String tablePrefix;
    private String fileName;
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

    public SQLite(CocoFFA plugin, String tablePrefix, String fileName) {
        this.plugin = plugin;
        this.tablePrefix = tablePrefix;
        this.fileName = fileName;
        this.setupDataSource();
    }

    private void setupDataSource() {
        File dataFolder = this.plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File databaseFile = new File(dataFolder, this.fileName);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(1);
        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public CompletableFuture<Void> initialize() {
        return CompletableFuture.runAsync(() -> {
            try {
                this.createTables();
            } catch (SQLException e) {
                this.plugin.getLogger().severe("Failed to create database tables: " + e.getMessage());
            }
        }, this.virtualThreadExecutor);
    }

    private void createTables() throws SQLException {
        try (Connection connection = this.dataSource.getConnection();){
            String playersTable = "CREATE TABLE IF NOT EXISTS " + this.tablePrefix + "players (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid VARCHAR(36) UNIQUE NOT NULL, name VARCHAR(16), wins INTEGER DEFAULT 0)";
            String inventoryTable = "CREATE TABLE IF NOT EXISTS " + this.tablePrefix + "saved_inventories (player_id INTEGER PRIMARY KEY, inventory TEXT, FOREIGN KEY (player_id) REFERENCES " + this.tablePrefix + "players(id))";
            String kitsTable = "CREATE TABLE IF NOT EXISTS " + this.tablePrefix + "arena_kits (id INTEGER PRIMARY KEY AUTOINCREMENT, arena_name VARCHAR(100) UNIQUE NOT NULL, kit_data TEXT, auto_armor_items TEXT, offhand_items TEXT, last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            try (PreparedStatement stmt1 = connection.prepareStatement(playersTable);
                 PreparedStatement stmt2 = connection.prepareStatement(inventoryTable);
                 PreparedStatement stmt3 = connection.prepareStatement(kitsTable);){
                stmt1.executeUpdate();
                stmt2.executeUpdate();
                stmt3.executeUpdate();
            }
        }
    }

    @Override
    public void shutdown() {
        CompletableFuture.runAsync(() -> {
            this.virtualThreadExecutor.shutdown();
            if (this.dataSource != null && !this.dataSource.isClosed()) {
                this.dataSource.close();
            }
        });
    }

    private Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public CompletableFuture<Integer> getOrCreatePlayerId(UUID playerUUID, String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = this.getConnection();){
                block42: {
                    String selectSql = "SELECT id FROM " + this.tablePrefix + "players WHERE uuid = ?";
                    try (PreparedStatement selectStmt = connection.prepareStatement(selectSql);){
                        selectStmt.setString(1, playerUUID.toString());
                        try (ResultSet rs = selectStmt.executeQuery();){
                            if (!rs.next()) break block42;
                            int playerId = rs.getInt("id");
                            String updateSql = "UPDATE " + this.tablePrefix + "players SET name = ? WHERE id = ?";
                            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql);){
                                updateStmt.setString(1, playerName);
                                updateStmt.setInt(2, playerId);
                                updateStmt.executeUpdate();
                            }
                            Integer n = playerId;
                            return n;
                        }
                    }
                }
                String insertSql = "INSERT INTO " + this.tablePrefix + "players (uuid, name, wins) VALUES (?, ?, 0)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql, 1);){
                    insertStmt.setString(1, playerUUID.toString());
                    insertStmt.setString(2, playerName);
                    insertStmt.executeUpdate();
                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys();){
                        if (!generatedKeys.next()) return -1;
                        Integer n = generatedKeys.getInt(1);
                        return n;
                    }
                }
            } catch (SQLException e) {
                this.plugin.getLogger().severe("Failed to get or create player ID for " + String.valueOf(playerUUID) + ": " + e.getMessage());
            }
            return -1;
        }, this.virtualThreadExecutor);
    }

    @Override
    public CompletableFuture<Void> saveInventory(UUID playerUUID, String playerName, String inventoryData) {
        return CompletableFuture.runAsync(() -> this.getOrCreatePlayerId(playerUUID, playerName).thenAccept(playerId -> {
            if (playerId == -1) {
                return;
            }
            try (Connection connection = this.getConnection();){
                String sql = "INSERT OR REPLACE INTO " + this.tablePrefix + "saved_inventories (player_id, inventory) VALUES (?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql);){
                    stmt.setInt(1, (int)playerId);
                    stmt.setString(2, inventoryData);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                this.plugin.getLogger().severe("Failed to save inventory for player ID " + playerId + ": " + e.getMessage());
            }
        }), this.virtualThreadExecutor);
    }

    @Override
    public CompletableFuture<String> getInventory(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = this.getConnection();){
                String sql = "SELECT si.inventory FROM " + this.tablePrefix + "saved_inventories si JOIN " + this.tablePrefix + "players p ON si.player_id = p.id WHERE p.uuid = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql);){
                    stmt.setString(1, playerUUID.toString());
                    try (ResultSet rs = stmt.executeQuery();){
                        if (!rs.next()) return null;
                        String string = rs.getString("inventory");
                        return string;
                    }
                }
            } catch (SQLException e) {
                this.plugin.getLogger().severe("Failed to get inventory for " + String.valueOf(playerUUID) + ": " + e.getMessage());
            }
            return null;
        }, this.virtualThreadExecutor);
    }

    @Override
    public CompletableFuture<Void> deleteInventory(UUID playerUUID) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = this.getConnection();){
                String sql = "DELETE FROM " + this.tablePrefix + "saved_inventories WHERE player_id = (SELECT id FROM " + this.tablePrefix + "players WHERE uuid = ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql);){
                    stmt.setString(1, playerUUID.toString());
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                this.plugin.getLogger().severe("Failed to delete inventory for " + String.valueOf(playerUUID) + ": " + e.getMessage());
            }
        }, this.virtualThreadExecutor);
    }

    @Override
    public CompletableFuture<Void> addWin(UUID playerUUID, String playerName) {
        return CompletableFuture.runAsync(() -> this.getOrCreatePlayerId(playerUUID, playerName).thenAccept(playerId -> {
            if (playerId == -1) {
                return;
            }
            try (Connection connection = this.getConnection();){
                String sql = "UPDATE " + this.tablePrefix + "players SET wins = wins + 1 WHERE id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql);){
                    stmt.setInt(1, (int)playerId);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                this.plugin.getLogger().severe("Failed to add win for player ID " + playerId + ": " + e.getMessage());
            }
        }), this.virtualThreadExecutor);
    }

    @Override
    public CompletableFuture<Integer> getWins(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = this.getConnection();){
                String sql = "SELECT wins FROM " + this.tablePrefix + "players WHERE uuid = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql);){
                    stmt.setString(1, playerUUID.toString());
                    try (ResultSet rs = stmt.executeQuery();){
                        if (!rs.next()) return 0;
                        Integer n = rs.getInt("wins");
                        return n;
                    }
                }
            } catch (SQLException e) {
                this.plugin.getLogger().severe("Failed to get wins for " + String.valueOf(playerUUID) + ": " + e.getMessage());
            }
            return 0;
        }, this.virtualThreadExecutor);
    }

    @Override
    public CompletableFuture<Boolean> setWins(UUID playerUUID, int wins) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = this.getConnection();){
                Boolean bl;
                block14: {
                    String sql = "UPDATE " + this.tablePrefix + "players SET wins = ? WHERE uuid = ?";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    try {
                        stmt.setInt(1, wins);
                        stmt.setString(2, playerUUID.toString());
                        int rowsAffected = stmt.executeUpdate();
                        bl = rowsAffected > 0;
                        if (stmt == null) break block14;
                    } catch (Throwable throwable) {
                        if (stmt != null) {
                            try {
                                stmt.close();
                            } catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    stmt.close();
                }
                return bl;
            } catch (SQLException e) {
                this.plugin.getLogger().severe("Failed to set wins for " + String.valueOf(playerUUID) + ": " + e.getMessage());
                return false;
            }
        }, this.virtualThreadExecutor);
    }

    @Override
    public CompletableFuture<Void> restoreAllInventories() {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = this.getConnection();){
                block30: {
                    String sql = "SELECT p.uuid, si.inventory FROM " + this.tablePrefix + "saved_inventories si JOIN " + this.tablePrefix + "players p ON si.player_id = p.id";
                    try (PreparedStatement stmt = connection.prepareStatement(sql);){
                        ResultSet rs = stmt.executeQuery();
                        block24: while (true) {
                            while (rs.next()) {
                                String uuidString = rs.getString("uuid");
                                String inventoryData = rs.getString("inventory");
                                try {
                                    UUID playerUUID = UUID.fromString(uuidString);
                                    this.plugin.getUniversalScheduler().runTask(() -> this.plugin.getInventoryManager().restorePlayerInventory(playerUUID, inventoryData));
                                    continue block24;
                                } catch (IllegalArgumentException e) {
                                    this.plugin.getLogger().warning("Invalid UUID in database: " + uuidString);
                                }
                            }
                            break block30;
                            {
                                continue block24;
                                break;
                            }
                            break;
                        }
                        finally {
                            if (rs != null) {
                                rs.close();
                            }
                        }
                    }
                }
                String clearSql = "DELETE FROM " + this.tablePrefix + "saved_inventories";
                try (PreparedStatement clearStmt = connection.prepareStatement(clearSql);){
                    clearStmt.executeUpdate();
                }
            } catch (SQLException e) {
                this.plugin.getLogger().severe("Failed to restore inventories: " + e.getMessage());
            }
        }, this.virtualThreadExecutor);
    }

    @Override
    public CompletableFuture<List<PlaceholderManager.ToplistEntry>> getTopWins(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            ArrayList<PlaceholderManager.ToplistEntry> topList;
            block24: {
                topList = new ArrayList<PlaceholderManager.ToplistEntry>();
                try (Connection connection = this.getConnection();){
                    String sql = "SELECT uuid, name, wins FROM " + this.tablePrefix + "players WHERE wins > 0 ORDER BY wins DESC LIMIT ?";
                    try (PreparedStatement stmt = connection.prepareStatement(sql);){
                        stmt.setInt(1, limit);
                        ResultSet rs = stmt.executeQuery();
                        block19: while (true) {
                            while (rs.next()) {
                                String uuidString = rs.getString("uuid");
                                String playerName = rs.getString("name");
                                int wins = rs.getInt("wins");
                                try {
                                    UUID playerUUID = UUID.fromString(uuidString);
                                    if (playerName == null) continue block19;
                                    topList.add(new PlaceholderManager.ToplistEntry(playerUUID, playerName, wins));
                                    continue block19;
                                } catch (IllegalArgumentException e) {
                                    this.plugin.getLogger().warning("Invalid UUID in players table: " + uuidString);
                                }
                            }
                            break block24;
                            {
                                continue block19;
                                break;
                            }
                            break;
                        }
                        finally {
                            if (rs != null) {
                                rs.close();
                            }
                        }
                    }
                } catch (SQLException e) {
                    this.plugin.getLogger().severe("Failed to get top wins: " + e.getMessage());
                }
            }
            return topList;
        }, this.virtualThreadExecutor);
    }

    @Override
    public LinkedHashMap<String, Integer> getTopPlayers(Long timePeriod) {
        LinkedHashMap<String, Integer> topPlayers = new LinkedHashMap<String, Integer>();
        try (Connection connection = this.getConnection();){
            String sql = timePeriod == null ? "SELECT name, wins FROM " + this.tablePrefix + "players WHERE wins > 0 ORDER BY wins DESC LIMIT 10" : "SELECT name, wins FROM " + this.tablePrefix + "players WHERE wins > 0 ORDER BY wins DESC LIMIT 10";
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery();){
                while (rs.next()) {
                    String playerName = rs.getString("name");
                    int wins = rs.getInt("wins");
                    if (playerName == null) continue;
                    topPlayers.put(playerName, wins);
                }
            }
        } catch (SQLException e) {
            this.plugin.getLogger().severe("Failed to get top players: " + e.getMessage());
        }
        return topPlayers;
    }

    @Override
    public CompletableFuture<Void> saveArenaKit(String arenaName, String kitData, String autoArmorItems, String offhandItems) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = this.getConnection();){
                String sql = "INSERT OR REPLACE INTO " + this.tablePrefix + "arena_kits (arena_name, kit_data, auto_armor_items, offhand_items) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql);){
                    stmt.setString(1, arenaName);
                    stmt.setString(2, kitData != null ? kitData : "");
                    stmt.setString(3, autoArmorItems != null ? autoArmorItems : "");
                    stmt.setString(4, offhandItems != null ? offhandItems : "");
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                this.plugin.getLogger().severe("Failed to save kit for arena " + arenaName + ": " + e.getMessage());
            }
        }, this.virtualThreadExecutor);
    }

    @Override
    public CompletableFuture<Database.KitData> getArenaKit(String arenaName) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = this.getConnection();){
                String sql = "SELECT kit_data, auto_armor_items, offhand_items FROM " + this.tablePrefix + "arena_kits WHERE arena_name = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql);){
                    stmt.setString(1, arenaName);
                    try (ResultSet rs = stmt.executeQuery();){
                        if (!rs.next()) return null;
                        String kitData = rs.getString("kit_data");
                        String autoArmorItems = rs.getString("auto_armor_items");
                        String offhandItems = rs.getString("offhand_items");
                        Database.KitData kitData2 = new Database.KitData(kitData, autoArmorItems, offhandItems);
                        return kitData2;
                    }
                }
            } catch (SQLException e) {
                this.plugin.getLogger().severe("Failed to get kit for arena " + arenaName + ": " + e.getMessage());
            }
            return null;
        }, this.virtualThreadExecutor);
    }

    @Override
    public CompletableFuture<Boolean> deleteArenaKit(String arenaName) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = this.getConnection();){
                Boolean bl;
                block14: {
                    String sql = "DELETE FROM " + this.tablePrefix + "arena_kits WHERE arena_name = ?";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    try {
                        stmt.setString(1, arenaName);
                        int rowsAffected = stmt.executeUpdate();
                        bl = rowsAffected > 0;
                        if (stmt == null) break block14;
                    } catch (Throwable throwable) {
                        if (stmt != null) {
                            try {
                                stmt.close();
                            } catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    stmt.close();
                }
                return bl;
            } catch (SQLException e) {
                this.plugin.getLogger().severe("Failed to delete kit for arena " + arenaName + ": " + e.getMessage());
                return false;
            }
        }, this.virtualThreadExecutor);
    }
}

