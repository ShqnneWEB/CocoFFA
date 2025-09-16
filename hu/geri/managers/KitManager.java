/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package hu.geri.managers;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.database.Database;
import hu.geri.utils.InventoryUtils;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class KitManager {
    private final CocoFFA plugin;
    private final Map<String, CachedKitData> kitCache = new ConcurrentHashMap<String, CachedKitData>();

    public KitManager(CocoFFA plugin) {
        this.plugin = plugin;
    }

    public void saveKit(String arenaName, ItemStack[] contents) {
        this.saveKit(arenaName, contents, null);
    }

    public void saveKit(String arenaName, ItemStack[] contents, boolean[] offhandFlags) {
        this.saveKit(arenaName, contents, offhandFlags, null);
    }

    public void saveKit(String arenaName, ItemStack[] contents, boolean[] offhandFlags, boolean[] autoArmorFlags) {
        try {
            String kitData = InventoryUtils.toBase64(contents);
            StringBuilder offhandData = new StringBuilder();
            if (offhandFlags != null) {
                for (int i = 0; i < offhandFlags.length; ++i) {
                    if (!offhandFlags[i]) continue;
                    if (offhandData.length() > 0) {
                        offhandData.append(",");
                    }
                    offhandData.append(i);
                }
            }
            StringBuilder autoArmorData = new StringBuilder();
            if (autoArmorFlags != null) {
                for (int i = 0; i < autoArmorFlags.length; ++i) {
                    if (!autoArmorFlags[i]) continue;
                    if (autoArmorData.length() > 0) {
                        autoArmorData.append(",");
                    }
                    autoArmorData.append(i);
                }
            }
            this.plugin.getDatabase().saveArenaKit(arenaName, kitData, autoArmorData.toString(), offhandData.toString()).get();
            this.clearKitCache(arenaName);
        } catch (Exception e) {
            this.plugin.getLogger().severe("Failed to save kit to database for arena " + arenaName + ": " + e.getMessage());
        }
    }

    public ItemStack[] loadKit(String arenaName) {
        return this.loadKit(arenaName, false);
    }

    public ItemStack[] loadKit(String arenaName, boolean loadForEditor) {
        try {
            Database.KitData kitData = this.plugin.getDatabase().getArenaKit(arenaName).get();
            if (kitData == null || kitData.getKitData() == null || kitData.getKitData().isEmpty()) {
                return null;
            }
            ItemStack[] kit = InventoryUtils.fromBase64(kitData.getKitData());
            if (loadForEditor && kit.length < 54) {
                ItemStack[] expandedKit = new ItemStack[54];
                System.arraycopy(kit, 0, expandedKit, 0, kit.length);
                kit = expandedKit;
            }
            return kit;
        } catch (Exception e) {
            this.plugin.getLogger().severe("Failed to load kit from database for arena " + arenaName + ": " + e.getMessage());
            return null;
        }
    }

    public boolean[] loadKitOffhandFlags(String arenaName) {
        try {
            Database.KitData kitData = this.plugin.getDatabase().getArenaKit(arenaName).get();
            boolean[] offhandFlags = new boolean[54];
            if (kitData != null && kitData.getOffhandItems() != null && !kitData.getOffhandItems().isEmpty()) {
                String[] offhandSlots;
                for (String slotStr : offhandSlots = kitData.getOffhandItems().split(",")) {
                    try {
                        int slot = Integer.parseInt(slotStr.trim());
                        if (slot < 0 || slot >= offhandFlags.length) continue;
                        offhandFlags[slot] = true;
                    } catch (NumberFormatException e) {
                        this.plugin.getLogger().warning("Invalid offhand slot number: " + slotStr);
                    }
                }
            }
            return offhandFlags;
        } catch (Exception e) {
            this.plugin.getLogger().severe("Failed to load offhand flags from database for arena " + arenaName + ": " + e.getMessage());
            return new boolean[54];
        }
    }

    public boolean[] loadKitAutoArmorFlags(String arenaName) {
        try {
            Database.KitData kitData = this.plugin.getDatabase().getArenaKit(arenaName).get();
            boolean[] autoArmorFlags = new boolean[54];
            if (kitData != null && kitData.getAutoArmorItems() != null && !kitData.getAutoArmorItems().isEmpty()) {
                String[] autoArmorSlots;
                for (String slotStr : autoArmorSlots = kitData.getAutoArmorItems().split(",")) {
                    try {
                        int slot = Integer.parseInt(slotStr.trim());
                        if (slot < 0 || slot >= autoArmorFlags.length) continue;
                        autoArmorFlags[slot] = true;
                    } catch (NumberFormatException e) {
                        this.plugin.getLogger().warning("Invalid auto armor slot number: " + slotStr);
                    }
                }
            }
            return autoArmorFlags;
        } catch (Exception e) {
            this.plugin.getLogger().severe("Failed to load auto armor flags from database for arena " + arenaName + ": " + e.getMessage());
            return new boolean[54];
        }
    }

    public void createKit(@NotNull String arenaName) {
        Arena arena = this.plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            return;
        }
        ItemStack[] emptyKit = new ItemStack[36];
        this.saveKit(arenaName, emptyKit);
        this.plugin.getLogger().info("Empty kit created for arena " + arenaName);
    }

    public boolean deleteKit(@NotNull String arenaName) {
        try {
            boolean deleted = this.plugin.getDatabase().deleteArenaKit(arenaName).get();
            if (deleted) {
                this.plugin.getLogger().info("Kit deleted from database for arena " + arenaName);
                File kitFile = new File(this.plugin.getDataFolder(), "kits/" + arenaName + ".yml");
                if (kitFile.exists()) {
                    kitFile.delete();
                    this.plugin.getLogger().info("YAML kit file also deleted for arena " + arenaName);
                }
            }
            return deleted;
        } catch (Exception e) {
            this.plugin.getLogger().severe("Failed to delete kit from database for arena " + arenaName + ": " + e.getMessage());
            return false;
        }
    }

    public void toggleAutoArmor(@NotNull String arenaName) {
        Arena arena = this.plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            return;
        }
        boolean newState = !arena.isAutoArmorEnabled();
        arena.setAutoArmorEnabled(newState);
        this.plugin.getArenaManager().saveArena(arena);
        this.plugin.getLogger().info("Auto armor " + (newState ? "enabled" : "disabled") + " for arena " + arenaName);
    }

    private int countItems(ItemStack[] items) {
        int count = 0;
        if (items != null) {
            for (ItemStack item : items) {
                if (item == null || item.getType().isAir()) continue;
                ++count;
            }
        }
        return count;
    }

    public void loadKitToCache(String arenaName) {
        try {
            ItemStack[] kit = this.loadKit(arenaName);
            boolean[] offhandFlags = this.loadKitOffhandFlags(arenaName);
            boolean[] autoArmorFlags = this.loadKitAutoArmorFlags(arenaName);
            if (kit != null) {
                this.kitCache.put(arenaName, new CachedKitData(kit, offhandFlags, autoArmorFlags));
                this.plugin.getLogger().info("Kit cached for arena: " + arenaName);
            }
        } catch (Exception e) {
            this.plugin.getLogger().severe("Failed to cache kit for arena " + arenaName + ": " + e.getMessage());
        }
    }

    public CachedKitData getCachedKit(String arenaName) {
        return this.kitCache.get(arenaName);
    }

    public void clearKitCache(String arenaName) {
        this.kitCache.remove(arenaName);
    }

    public void clearAllKitCache() {
        this.kitCache.clear();
    }

    public static class CachedKitData {
        public final ItemStack[] kit;
        public final boolean[] offhandFlags;
        public final boolean[] autoArmorFlags;

        public CachedKitData(ItemStack[] kit, boolean[] offhandFlags, boolean[] autoArmorFlags) {
            this.kit = kit;
            this.offhandFlags = offhandFlags;
            this.autoArmorFlags = autoArmorFlags;
        }
    }
}

