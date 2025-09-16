/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 */
package hu.geri.managers;

import hu.geri.CocoFFA;
import hu.geri.managers.KitManager;
import hu.geri.utils.InventoryUtils;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryManager {
    private final CocoFFA plugin;
    private final Map<UUID, ItemStack[]> pendingRestores;

    public InventoryManager(CocoFFA plugin) {
        this.plugin = plugin;
        this.pendingRestores = new ConcurrentHashMap<UUID, ItemStack[]>();
    }

    public void savePlayerInventory(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        String inventoryData = InventoryUtils.toBase64(contents);
        this.plugin.getDatabase().saveInventory(player.getUniqueId(), player.getName(), inventoryData);
    }

    public void restorePlayerInventory(Player player) {
        this.restorePlayerInventory(player, true);
    }

    public void restorePlayerInventory(Player player, boolean withDelay) {
        this.plugin.getDatabase().getInventory(player.getUniqueId()).thenAccept(inventoryData -> {
            if (inventoryData != null) {
                this.plugin.getUniversalScheduler().runTask((Entity)player, () -> {
                    if (withDelay) {
                        this.restoreWithDelay(player, (String)inventoryData);
                    } else {
                        this.restoreImmediately(player, (String)inventoryData);
                    }
                });
            } else {
                this.checkAndRestorePending(player);
            }
        });
    }

    public void restorePlayerInventory(UUID playerUUID, String inventoryData) {
        Player player = Bukkit.getPlayer((UUID)playerUUID);
        if (player != null && player.isOnline()) {
            this.plugin.getUniversalScheduler().runTask((Entity)player, () -> this.restoreImmediately(player, inventoryData));
        } else {
            try {
                ItemStack[] contents = InventoryUtils.fromBase64(inventoryData);
                this.pendingRestores.put(playerUUID, contents);
            } catch (Exception e) {
                this.plugin.getLogger().warning("Failed to parse inventory data for " + String.valueOf(playerUUID) + ": " + e.getMessage());
            }
        }
    }

    public void restoreOfflinePlayerInventory(Player player) {
        this.plugin.getDatabase().getInventory(player.getUniqueId()).thenAccept(inventoryData -> {
            if (inventoryData != null) {
                try {
                    ItemStack[] contents = InventoryUtils.fromBase64(inventoryData);
                    this.pendingRestores.put(player.getUniqueId(), contents);
                    this.plugin.getUniversalScheduler().runTaskLater((Entity)player, () -> {
                        if (player.isOnline()) {
                            player.getInventory().clear();
                            player.getInventory().setContents(contents);
                            player.updateInventory();
                            this.pendingRestores.remove(player.getUniqueId());
                            this.plugin.getDatabase().deleteInventory(player.getUniqueId());
                            this.plugin.getLogger().info("Restored inventory for player: " + player.getName());
                        }
                    }, 20L);
                } catch (Exception e) {
                    this.plugin.getLogger().warning("Failed to restore offline inventory for " + player.getName() + ": " + e.getMessage());
                }
            }
        });
    }

    private void restoreWithDelay(Player player, String inventoryData) {
        player.getInventory().clear();
        player.updateInventory();
        this.plugin.getUniversalScheduler().runTaskLater((Entity)player, () -> {
            if (player.isOnline()) {
                this.restoreImmediately(player, inventoryData);
                this.plugin.getDatabase().deleteInventory(player.getUniqueId());
            }
        }, 40L);
    }

    private void restoreImmediately(Player player, String inventoryData) {
        try {
            ItemStack[] contents = InventoryUtils.fromBase64(inventoryData);
            player.getInventory().clear();
            player.getInventory().setContents(contents);
            player.updateInventory();
            this.plugin.getDatabase().deleteInventory(player.getUniqueId());
            this.pendingRestores.remove(player.getUniqueId());
        } catch (Exception e) {
            this.plugin.getLogger().warning("Failed to restore inventory for " + player.getName() + ": " + e.getMessage());
        }
    }

    public void handlePlayerJoin(Player player) {
        this.plugin.getDatabase().getInventory(player.getUniqueId()).thenAccept(inventoryData -> {
            if (inventoryData != null) {
                this.plugin.getUniversalScheduler().runTaskLater((Entity)player, () -> {
                    if (player.isOnline()) {
                        try {
                            ItemStack[] contents = InventoryUtils.fromBase64(inventoryData);
                            player.getInventory().clear();
                            player.getInventory().setContents(contents);
                            player.updateInventory();
                            this.plugin.getDatabase().deleteInventory(player.getUniqueId());
                            this.plugin.getLogger().info("Restored saved inventory for " + player.getName() + " on join");
                        } catch (Exception e) {
                            this.plugin.getLogger().warning("Failed to restore inventory on join for " + player.getName() + ": " + e.getMessage());
                        }
                    }
                }, 20L);
            } else {
                this.checkAndRestorePending(player);
            }
        });
    }

    private void checkAndRestorePending(Player player) {
        ItemStack[] pending = this.pendingRestores.remove(player.getUniqueId());
        if (pending != null) {
            this.plugin.getUniversalScheduler().runTaskLater((Entity)player, () -> {
                if (player.isOnline()) {
                    player.getInventory().clear();
                    player.getInventory().setContents(pending);
                    player.updateInventory();
                    this.plugin.getLogger().info("Restored pending inventory for " + player.getName());
                }
            }, 20L);
        }
    }

    public void clearPlayerInventory(Player player) {
        player.getInventory().clear();
        player.updateInventory();
    }

    public void giveKitToPlayer(Player player, String arenaName, boolean autoArmor) {
        boolean[] autoArmorFlags;
        boolean[] offhandFlags;
        ItemStack[] kit;
        KitManager.CachedKitData cachedKit = this.plugin.getKitManager().getCachedKit(arenaName);
        if (cachedKit != null) {
            kit = cachedKit.kit;
            offhandFlags = cachedKit.offhandFlags;
            autoArmorFlags = cachedKit.autoArmorFlags;
        } else {
            kit = this.plugin.getKitManager().loadKit(arenaName);
            offhandFlags = this.plugin.getKitManager().loadKitOffhandFlags(arenaName);
            autoArmorFlags = this.plugin.getKitManager().loadKitAutoArmorFlags(arenaName);
        }
        if (kit == null) {
            return;
        }
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        ItemStack helmet = null;
        ItemStack chestplate = null;
        ItemStack leggings = null;
        ItemStack boots = null;
        ItemStack offhandItem = null;
        for (int i = 0; i < kit.length; ++i) {
            String typeName;
            Material type;
            boolean isAutoArmorItem;
            ItemStack item = kit[i];
            if (item == null || item.getType().isAir()) continue;
            boolean isOffhand = offhandFlags != null && i < offhandFlags.length && offhandFlags[i];
            boolean bl = isAutoArmorItem = autoArmorFlags != null && i < autoArmorFlags.length && autoArmorFlags[i];
            if (isOffhand) {
                offhandItem = item.clone();
                continue;
            }
            if (isAutoArmorItem && this.isArmorPiece(item)) {
                type = item.getType();
                typeName = type.name();
                if ((typeName.contains("_HELMET") || typeName.equals("PLAYER_HEAD") || typeName.contains("SKULL")) && helmet == null) {
                    helmet = item.clone();
                    continue;
                }
                if ((typeName.contains("_CHESTPLATE") || typeName.equals("ELYTRA")) && chestplate == null) {
                    chestplate = item.clone();
                    continue;
                }
                if (typeName.contains("_LEGGINGS") && leggings == null) {
                    leggings = item.clone();
                    continue;
                }
                if (typeName.contains("_BOOTS") && boots == null) {
                    boots = item.clone();
                    continue;
                }
                inventory.addItem(new ItemStack[]{item.clone()});
                continue;
            }
            if (autoArmor && this.isArmorPiece(item)) {
                type = item.getType();
                typeName = type.name();
                if ((typeName.contains("_HELMET") || typeName.equals("PLAYER_HEAD") || typeName.contains("SKULL")) && helmet == null) {
                    helmet = item.clone();
                    continue;
                }
                if ((typeName.contains("_CHESTPLATE") || typeName.equals("ELYTRA")) && chestplate == null) {
                    chestplate = item.clone();
                    continue;
                }
                if (typeName.contains("_LEGGINGS") && leggings == null) {
                    leggings = item.clone();
                    continue;
                }
                if (typeName.contains("_BOOTS") && boots == null) {
                    boots = item.clone();
                    continue;
                }
                inventory.addItem(new ItemStack[]{item.clone()});
                continue;
            }
            inventory.addItem(new ItemStack[]{item.clone()});
        }
        if (autoArmor || helmet != null || chestplate != null || leggings != null || boots != null) {
            if (helmet != null) {
                inventory.setHelmet(helmet);
            }
            if (chestplate != null) {
                inventory.setChestplate(chestplate);
            }
            if (leggings != null) {
                inventory.setLeggings(leggings);
            }
            if (boots != null) {
                inventory.setBoots(boots);
            }
        }
        if (offhandItem != null) {
            inventory.setItemInOffHand(offhandItem);
        }
        player.updateInventory();
        this.plugin.getLogger().info("Kit given to player " + player.getName() + " for arena " + arenaName);
    }

    private boolean isArmorPiece(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }
        String typeName = item.getType().name();
        return typeName.contains("_HELMET") || typeName.contains("_CHESTPLATE") || typeName.contains("_LEGGINGS") || typeName.contains("_BOOTS") || typeName.equals("PLAYER_HEAD") || typeName.contains("SKULL") || typeName.equals("ELYTRA");
    }
}

