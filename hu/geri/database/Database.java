/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.database;

import hu.geri.managers.PlaceholderManager;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Database {
    public CompletableFuture<Void> initialize();

    public void shutdown();

    public CompletableFuture<Integer> getOrCreatePlayerId(UUID var1, String var2);

    public CompletableFuture<Void> saveInventory(UUID var1, String var2, String var3);

    public CompletableFuture<String> getInventory(UUID var1);

    public CompletableFuture<Void> deleteInventory(UUID var1);

    public CompletableFuture<Void> addWin(UUID var1, String var2);

    public CompletableFuture<Integer> getWins(UUID var1);

    public CompletableFuture<Boolean> setWins(UUID var1, int var2);

    public CompletableFuture<Void> restoreAllInventories();

    public CompletableFuture<List<PlaceholderManager.ToplistEntry>> getTopWins(int var1);

    public LinkedHashMap<String, Integer> getTopPlayers(Long var1);

    public CompletableFuture<Void> saveArenaKit(String var1, String var2, String var3, String var4);

    public CompletableFuture<KitData> getArenaKit(String var1);

    public CompletableFuture<Boolean> deleteArenaKit(String var1);

    public static class KitData {
        private final String kitData;
        private final String autoArmorItems;
        private final String offhandItems;

        public KitData(String kitData, String autoArmorItems, String offhandItems) {
            this.kitData = kitData;
            this.autoArmorItems = autoArmorItems;
            this.offhandItems = offhandItems;
        }

        public String getKitData() {
            return this.kitData;
        }

        public String getAutoArmorItems() {
            return this.autoArmorItems;
        }

        public String getOffhandItems() {
            return this.offhandItems;
        }
    }
}

