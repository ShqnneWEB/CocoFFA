/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.guis;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.guis.MainEditorGUI;
import hu.geri.libs.boostedyaml.YamlDocument;
import hu.geri.libs.boostedyaml.dvs.versioning.BasicVersioning;
import hu.geri.libs.boostedyaml.settings.dumper.DumperSettings;
import hu.geri.libs.boostedyaml.settings.general.GeneralSettings;
import hu.geri.libs.boostedyaml.settings.loader.LoaderSettings;
import hu.geri.libs.boostedyaml.settings.updater.UpdaterSettings;
import hu.geri.libs.gui.builder.gui.SimpleBuilder;
import hu.geri.libs.gui.builder.item.ItemBuilder;
import hu.geri.libs.gui.guis.Gui;
import hu.geri.libs.gui.guis.GuiItem;
import hu.geri.libs.universalScheduler.scheduling.tasks.MyScheduledTask;
import hu.geri.managers.KitManager;
import hu.geri.processor.MessageProcessor;
import hu.geri.utils.ItemTagUtils;
import hu.geri.utils.SkullUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class KitEditorGUI {
    private final CocoFFA plugin;
    private final KitManager kitManager;
    private YamlDocument guiConfig;
    private final Map<UUID, ItemStack[]> temporaryKits;
    private final Map<UUID, MyScheduledTask> updateTasks;
    private final Map<UUID, String> openGuis;

    public KitEditorGUI(CocoFFA plugin) {
        this.plugin = plugin;
        this.kitManager = plugin.getKitManager();
        this.temporaryKits = new HashMap<UUID, ItemStack[]>();
        this.updateTasks = new HashMap<UUID, MyScheduledTask>();
        this.openGuis = new HashMap<UUID, String>();
        this.loadGuiConfig();
    }

    private void loadGuiConfig() {
        try {
            File guiFile = new File(this.plugin.getDataFolder(), "guis/kit-editor-gui.yml");
            this.guiConfig = YamlDocument.create(guiFile, this.plugin.getResource("guis/kit-editor-gui.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("gui-version")).setKeepAll(true).build());
        } catch (IOException e) {
            this.plugin.getLogger().severe("Failed to load kit-editor-gui.yml with BoostedYAML: " + e.getMessage());
        }
    }

    public void openKitEditor(@NotNull Player player, @NotNull String arenaName) {
        Arena arena = this.plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.kit.arena-not-found", "{arena_name}", arenaName)));
            return;
        }
        if (!this.validateGuiConfiguration()) {
            player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.kit.config-error")));
            return;
        }
        MyScheduledTask existingTask = this.updateTasks.remove(player.getUniqueId());
        if (existingTask != null) {
            existingTask.cancel();
        }
        String title = MessageProcessor.process(this.guiConfig.getString("title", "&0Kit Editor - {arena}").replace("{arena}", arenaName));
        int rows = this.guiConfig.getInt("rows", (Integer)6);
        Gui gui = ((SimpleBuilder)((SimpleBuilder)Gui.gui().title(Component.text(title))).rows(rows)).create();
        this.openGuis.put(player.getUniqueId(), arenaName);
        this.loadCurrentKit(player, arena);
        this.setMenuItems(gui, player, arena);
        gui.open((HumanEntity)player);
        String openSound = this.guiConfig.getString("open-sound", "");
        if (!openSound.isEmpty()) {
            try {
                Sound sound = Sound.valueOf((String)openSound.toUpperCase());
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.kit.opened", "{arena_name}", arenaName)));
    }

    private void loadCurrentKit(@NotNull Player player, @NotNull Arena arena) {
        ItemStack[] currentKit = this.kitManager.loadKit(arena.getName(), true);
        boolean[] offhandFlags = this.kitManager.loadKitOffhandFlags(arena.getName());
        boolean[] autoArmorFlags = this.kitManager.loadKitAutoArmorFlags(arena.getName());
        int totalSlots = this.guiConfig.getInt("rows", (Integer)6) * 9;
        ItemStack[] expandedKit = new ItemStack[totalSlots];
        if (currentKit != null) {
            for (int i = 0; i < Math.min(currentKit.length, totalSlots); ++i) {
                if (currentKit[i] == null || currentKit[i].getType().isAir()) continue;
                ItemStack item = currentKit[i].clone();
                if (offhandFlags != null && i < offhandFlags.length && offhandFlags[i]) {
                    item = ItemTagUtils.setOffhandTag((Plugin)this.plugin, item, true);
                    this.addOffhandLore(item);
                }
                if (autoArmorFlags != null && i < autoArmorFlags.length && autoArmorFlags[i]) {
                    item = ItemTagUtils.setAutoArmorTag((Plugin)this.plugin, item, true);
                    this.addAutoArmorLore(item);
                }
                expandedKit[i] = item;
            }
        }
        this.temporaryKits.put(player.getUniqueId(), expandedKit);
    }

    private void setMenuItems(@NotNull Gui gui, @NotNull Player player, @NotNull Arena arena) {
        this.addControlButtons(gui, player, arena);
        this.addFillerItems(gui);
        this.loadKitItemsToGui(gui, player);
        gui.setCloseGuiAction(event -> {
            this.updateTemporaryKit(player, gui);
            this.cleanupPlayer(player);
            this.openGuis.remove(player.getUniqueId());
        });
        gui.setDefaultClickAction(event -> {
            int slot = event.getSlot();
            if (event.getClickedInventory() == player.getInventory()) {
                event.setCancelled(false);
                return;
            }
            if (this.isEditableSlot(slot)) {
                if (event.getClick() == ClickType.RIGHT) {
                    event.setCancelled(true);
                    this.handleOffhandToggle(player, gui, slot);
                } else if (event.getClick() == ClickType.SHIFT_RIGHT) {
                    event.setCancelled(true);
                    this.handleAutoArmorToggle(player, gui, slot);
                } else {
                    event.setCancelled(false);
                }
            } else {
                event.setCancelled(true);
            }
        });
        gui.setPlayerInventoryAction(event -> event.setCancelled(false));
        MyScheduledTask task = this.plugin.getUniversalScheduler().runTaskTimer((Entity)player, () -> {
            if (gui.getInventory().getViewers().contains(player)) {
                this.updateTemporaryKit(player, gui);
            } else {
                MyScheduledTask currentTask = this.updateTasks.remove(player.getUniqueId());
                if (currentTask != null) {
                    currentTask.cancel();
                }
            }
        }, 20L, 20L);
        this.updateTasks.put(player.getUniqueId(), task);
    }

    private void addControlButtons(@NotNull Gui gui, @NotNull Player player, @NotNull Arena arena) {
        this.addSaveButton(gui, player, arena);
        this.addCancelButton(gui, player, arena);
        this.addInfoButton(gui);
    }

    private void addSaveButton(@NotNull Gui gui, @NotNull Player player, @NotNull Arena arena) {
        GuiItem button;
        String path = "items.save";
        int slot = this.guiConfig.getInt(path + ".slot", (Integer)45);
        String materialName = this.guiConfig.getString(path + ".material", "LIME_WOOL");
        String name = this.guiConfig.getString(path + ".name", "&a&lSAVE");
        List<String> lore = this.guiConfig.getStringList(path + ".lore");
        if (SkullUtils.isBase64Material(materialName)) {
            ItemStack skull = SkullUtils.createSkullFromBase64(materialName);
            button = new GuiItem(skull, event -> {
                event.setCancelled(true);
                this.saveKit(player, gui, arena);
            });
        } else {
            try {
                Material material = Material.valueOf((String)materialName.toUpperCase());
                button = ItemBuilder.from(material).asGuiItem(event -> {
                    event.setCancelled(true);
                    this.saveKit(player, gui, arena);
                });
            } catch (IllegalArgumentException e) {
                this.plugin.getLogger().warning("Invalid material in kit-editor-gui.yml: " + materialName);
                return;
            }
        }
        ItemStack itemStack = button.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(MessageProcessor.process(name));
            ArrayList<String> coloredLore = new ArrayList<String>();
            for (String line : lore) {
                coloredLore.add(MessageProcessor.process(line));
            }
            itemMeta.setLore(coloredLore);
            itemStack.setItemMeta(itemMeta);
        }
        gui.setItem(slot, button);
    }

    private void addCancelButton(@NotNull Gui gui, @NotNull Player player, @NotNull Arena arena) {
        GuiItem button;
        String path = "items.cancel";
        int slot = this.guiConfig.getInt(path + ".slot", (Integer)53);
        String materialName = this.guiConfig.getString(path + ".material", "RED_WOOL");
        String name = this.guiConfig.getString(path + ".name", "&c&lCANCEL");
        List<String> lore = this.guiConfig.getStringList(path + ".lore");
        if (SkullUtils.isBase64Material(materialName)) {
            ItemStack skull = SkullUtils.createSkullFromBase64(materialName);
            button = new GuiItem(skull, event -> {
                event.setCancelled(true);
                this.cancelEdit(player, arena);
            });
        } else {
            try {
                Material material = Material.valueOf((String)materialName.toUpperCase());
                button = ItemBuilder.from(material).asGuiItem(event -> {
                    event.setCancelled(true);
                    this.cancelEdit(player, arena);
                });
            } catch (IllegalArgumentException e) {
                this.plugin.getLogger().warning("Invalid material in kit-editor-gui.yml: " + materialName);
                return;
            }
        }
        ItemStack itemStack = button.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(MessageProcessor.process(name));
            ArrayList<String> coloredLore = new ArrayList<String>();
            for (String line : lore) {
                coloredLore.add(MessageProcessor.process(line));
            }
            itemMeta.setLore(coloredLore);
            itemStack.setItemMeta(itemMeta);
        }
        gui.setItem(slot, button);
    }

    private void addInfoButton(@NotNull Gui gui) {
        GuiItem button;
        String path = "items.info";
        int slot = this.guiConfig.getInt(path + ".slot", (Integer)49);
        String materialName = this.guiConfig.getString(path + ".material", "BOOK");
        String name = this.guiConfig.getString(path + ".name", "&e&lINFO");
        List<String> lore = this.guiConfig.getStringList(path + ".lore");
        if (SkullUtils.isBase64Material(materialName)) {
            ItemStack skull = SkullUtils.createSkullFromBase64(materialName);
            button = new GuiItem(skull, event -> event.setCancelled(true));
        } else {
            try {
                Material material = Material.valueOf((String)materialName.toUpperCase());
                button = ItemBuilder.from(material).asGuiItem(event -> event.setCancelled(true));
            } catch (IllegalArgumentException e) {
                this.plugin.getLogger().warning("Invalid material in kit-editor-gui.yml: " + materialName);
                return;
            }
        }
        ItemStack itemStack = button.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(MessageProcessor.process(name));
            ArrayList<String> coloredLore = new ArrayList<String>();
            for (String line : lore) {
                coloredLore.add(MessageProcessor.process(line));
            }
            itemMeta.setLore(coloredLore);
            itemStack.setItemMeta(itemMeta);
        }
        gui.setItem(slot, button);
    }

    private void addFillerItems(@NotNull Gui gui) {
        GuiItem filler;
        if (!this.guiConfig.contains("filler.filler-slots")) {
            return;
        }
        List<Integer> fillerSlots = this.guiConfig.getIntList("filler.filler-slots");
        if (fillerSlots.isEmpty()) {
            return;
        }
        String materialName = this.guiConfig.getString("filler.material", "GRAY_STAINED_GLASS_PANE");
        String name = this.guiConfig.getString("filler.name", "&7");
        List<String> lore = this.guiConfig.getStringList("filler.lore");
        if (SkullUtils.isBase64Material(materialName)) {
            ItemStack skull = SkullUtils.createSkullFromBase64(materialName);
            filler = new GuiItem(skull, event -> event.setCancelled(true));
        } else {
            try {
                Material material = Material.valueOf((String)materialName.toUpperCase());
                filler = ItemBuilder.from(material).asGuiItem(event -> event.setCancelled(true));
            } catch (IllegalArgumentException e) {
                this.plugin.getLogger().warning("Invalid filler material in kit-editor-gui.yml: " + materialName);
                return;
            }
        }
        ItemStack itemStack = filler.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(MessageProcessor.process(name));
            ArrayList<String> processedLore = new ArrayList<String>();
            for (String line : lore) {
                processedLore.add(MessageProcessor.process(line));
            }
            itemMeta.setLore(processedLore);
            itemStack.setItemMeta(itemMeta);
        }
        for (int slot : fillerSlots) {
            if (slot < 0 || slot >= gui.getRows() * 9) continue;
            gui.setItem(slot, filler);
        }
    }

    private void saveKit(@NotNull Player player, @NotNull Gui gui, @NotNull Arena arena) {
        this.updateTemporaryKit(player, gui);
        ItemStack[] tempKit = this.temporaryKits.get(player.getUniqueId());
        if (tempKit == null) {
            player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.kit.save-error")));
            return;
        }
        ItemStack[] kitToSave = new ItemStack[54];
        boolean[] offhandFlags = new boolean[54];
        boolean[] autoArmorFlags = new boolean[54];
        for (int slot = 0; slot < tempKit.length; ++slot) {
            boolean isAutoArmor;
            if (!this.isEditableSlot(slot) || tempKit[slot] == null || tempKit[slot].getType().isAir()) continue;
            ItemStack item = tempKit[slot].clone();
            boolean isOffhand = ItemTagUtils.hasOffhandTag((Plugin)this.plugin, item);
            if (isOffhand) {
                this.removeOffhandLore(item);
                item = ItemTagUtils.removeOffhandTag((Plugin)this.plugin, item);
                offhandFlags[slot] = true;
            }
            if (isAutoArmor = ItemTagUtils.hasAutoArmorTag((Plugin)this.plugin, item)) {
                this.removeAutoArmorLore(item);
                item = ItemTagUtils.removeAutoArmorTag((Plugin)this.plugin, item);
                autoArmorFlags[slot] = true;
            }
            kitToSave[slot] = item;
        }
        this.kitManager.saveKit(arena.getName(), kitToSave, offhandFlags, autoArmorFlags);
        this.cleanupPlayer(player);
        this.openGuis.remove(player.getUniqueId());
        player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.kit.saved", "{arena_name}", arena.getName())));
        new MainEditorGUI(this.plugin).openMainEditor(player, arena);
    }

    private boolean hasOffhandLore(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasLore()) {
            List lore = meta.getLore();
            List<String> configOffhandLore = this.guiConfig.getStringList("offhand.lore");
            ArrayList<String> processedOffhandLore = new ArrayList<String>();
            for (String loreLine : configOffhandLore) {
                processedOffhandLore.add(MessageProcessor.process(loreLine));
            }
            int offhandLoreSize = processedOffhandLore.size();
            if (lore.size() >= offhandLoreSize) {
                for (int i = 0; i < offhandLoreSize; ++i) {
                    String expectedLine;
                    int loreIndex = lore.size() - offhandLoreSize + i;
                    String currentLine = (String)lore.get(loreIndex);
                    if (currentLine.equals(expectedLine = (String)processedOffhandLore.get(i))) continue;
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private void cancelEdit(@NotNull Player player, @NotNull Arena arena) {
        this.cleanupPlayer(player);
        this.openGuis.remove(player.getUniqueId());
        player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.kit.cancelled")));
        new MainEditorGUI(this.plugin).openMainEditor(player, arena);
    }

    private boolean validateGuiConfiguration() {
        boolean hasConflict = false;
        if (!this.guiConfig.contains("fillers") || !this.guiConfig.contains("buttons")) {
            return true;
        }
        ArrayList<Integer> buttonSlots = new ArrayList<Integer>();
        String[] buttons = new String[]{"save", "cancel", "info", "auto-armor-toggle"};
        for (String button : buttons) {
            int buttonSlot = this.guiConfig.getInt("buttons." + button + ".slot", (Integer)-1);
            if (buttonSlot == -1) continue;
            buttonSlots.add(buttonSlot);
        }
        for (String fillerKey : this.guiConfig.getSection("fillers").getRoutesAsStrings(false)) {
            String path = "fillers." + fillerKey;
            List<Integer> fillerSlots = this.guiConfig.getIntList(path + ".slots");
            for (int slot : fillerSlots) {
                if (!buttonSlots.contains(slot)) continue;
                this.plugin.getLogger().severe("GUI Configuration Error: Filler '" + fillerKey + "' has slot " + slot + " which conflicts with button slot!");
                hasConflict = true;
            }
        }
        if (hasConflict) {
            this.plugin.getLogger().severe("Kit Editor GUI cannot be opened due to configuration conflicts!");
            this.plugin.getLogger().severe("Please fix the conflicts in guis/kit-editor-gui.yml and reload the plugin.");
            return false;
        }
        return true;
    }

    private void loadKitItemsToGui(@NotNull Gui gui, @NotNull Player player) {
        ItemStack[] tempKit = this.temporaryKits.get(player.getUniqueId());
        if (tempKit != null) {
            int totalSlots = gui.getRows() * 9;
            for (int slot = 0; slot < Math.min(tempKit.length, totalSlots); ++slot) {
                if (!this.isEditableSlot(slot) || tempKit[slot] == null || tempKit[slot].getType().isAir()) continue;
                gui.setItem(slot, new GuiItem(tempKit[slot].clone()));
            }
        }
    }

    private boolean isEditableSlot(int slot) {
        int startSlot = this.guiConfig.getInt("editable_area.start_slot", (Integer)9);
        int endSlot = this.guiConfig.getInt("editable_area.end_slot", (Integer)44);
        return slot >= startSlot && slot <= endSlot && !this.isButtonSlot(slot) && !this.isFillerSlot(slot);
    }

    private boolean isButtonSlot(int slot) {
        return slot == this.guiConfig.getInt("items.save.slot", (Integer)45) || slot == this.guiConfig.getInt("items.cancel.slot", (Integer)53) || slot == this.guiConfig.getInt("items.info.slot", (Integer)49);
    }

    private boolean isFillerSlot(int slot) {
        if (!this.guiConfig.contains("filler.filler-slots")) {
            return false;
        }
        List<Integer> fillerSlots = this.guiConfig.getIntList("filler.filler-slots");
        return fillerSlots.contains(slot);
    }

    private void updateTemporaryKit(@NotNull Player player, @NotNull Gui gui) {
        int totalSlots = gui.getRows() * 9;
        ItemStack[] tempKit = new ItemStack[totalSlots];
        for (int slot = 0; slot < totalSlots; ++slot) {
            ItemStack item;
            if (!this.isEditableSlot(slot) || (item = gui.getInventory().getItem(slot)) == null || item.getType().isAir()) continue;
            tempKit[slot] = item.clone();
        }
        this.temporaryKits.put(player.getUniqueId(), tempKit);
    }

    private void handleOffhandToggle(@NotNull Player player, @NotNull Gui gui, int slot) {
        ItemStack clickedItem = gui.getInventory().getItem(slot);
        if (clickedItem == null || clickedItem.getType().isAir()) {
            return;
        }
        boolean isCurrentlyOffhand = ItemTagUtils.hasOffhandTag((Plugin)this.plugin, clickedItem);
        if (isCurrentlyOffhand) {
            ItemStack updatedItem = ItemTagUtils.removeOffhandTag((Plugin)this.plugin, clickedItem);
            this.removeOffhandLore(updatedItem);
            gui.updateItem(slot, new GuiItem(updatedItem));
            player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.kit.offhand-removed")));
        } else {
            ItemStack updatedItem = ItemTagUtils.setOffhandTag((Plugin)this.plugin, clickedItem, true);
            this.addOffhandLore(updatedItem);
            gui.updateItem(slot, new GuiItem(updatedItem));
            player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.kit.offhand-added")));
        }
        this.clearAllOtherOffhandItems(gui, player, slot);
    }

    private void clearAllOtherOffhandItems(@NotNull Gui gui, @NotNull Player player, int excludeSlot) {
        int startSlot = this.guiConfig.getInt("editable_area.start_slot", (Integer)9);
        int endSlot = this.guiConfig.getInt("editable_area.end_slot", (Integer)44);
        for (int i = startSlot; i <= endSlot; ++i) {
            boolean hasNBTTag;
            ItemStack item;
            if (i == excludeSlot || (item = gui.getInventory().getItem(i)) == null || item.getType().isAir() || !(hasNBTTag = ItemTagUtils.hasOffhandTag((Plugin)this.plugin, item))) continue;
            ItemStack updatedItem = ItemTagUtils.removeOffhandTag((Plugin)this.plugin, item);
            this.removeOffhandLore(updatedItem);
            gui.updateItem(i, new GuiItem(updatedItem));
        }
    }

    private void addOffhandLore(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            ArrayList<String> lore = meta.hasLore() ? new ArrayList<String>(meta.getLore()) : new ArrayList();
            List<String> offhandLore = this.guiConfig.getStringList("offhand.lore");
            for (String loreLine : offhandLore) {
                lore.add(MessageProcessor.process(loreLine));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    private void removeOffhandLore(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasLore()) {
            ArrayList lore = new ArrayList(meta.getLore());
            List<String> configOffhandLore = this.guiConfig.getStringList("offhand.lore");
            ArrayList<String> processedOffhandLore = new ArrayList<String>();
            for (String loreLine : configOffhandLore) {
                processedOffhandLore.add(MessageProcessor.process(loreLine));
            }
            int offhandLoreSize = processedOffhandLore.size();
            if (lore.size() >= offhandLoreSize) {
                int i;
                boolean isOffhandLore = true;
                for (i = 0; i < offhandLoreSize; ++i) {
                    String expectedLine;
                    int loreIndex = lore.size() - offhandLoreSize + i;
                    String currentLine = (String)lore.get(loreIndex);
                    if (currentLine.equals(expectedLine = (String)processedOffhandLore.get(i))) continue;
                    isOffhandLore = false;
                    break;
                }
                if (isOffhandLore) {
                    for (i = 0; i < offhandLoreSize; ++i) {
                        lore.remove(lore.size() - 1);
                    }
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
            }
        }
    }

    private void handleAutoArmorToggle(@NotNull Player player, @NotNull Gui gui, int slot) {
        ItemStack clickedItem = gui.getInventory().getItem(slot);
        if (clickedItem == null || clickedItem.getType().isAir()) {
            return;
        }
        if (!this.isArmorPiece(clickedItem)) {
            player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.kit.auto-armor-not-armor")));
            return;
        }
        boolean isCurrentlyAutoArmor = ItemTagUtils.hasAutoArmorTag((Plugin)this.plugin, clickedItem);
        if (isCurrentlyAutoArmor) {
            ItemStack updatedItem = ItemTagUtils.removeAutoArmorTag((Plugin)this.plugin, clickedItem);
            this.removeAutoArmorLore(updatedItem);
            gui.updateItem(slot, new GuiItem(updatedItem));
            player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.kit.auto-armor-removed")));
        } else {
            ItemStack updatedItem = ItemTagUtils.setAutoArmorTag((Plugin)this.plugin, clickedItem, true);
            this.addAutoArmorLore(updatedItem);
            gui.updateItem(slot, new GuiItem(updatedItem));
            player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.kit.auto-armor-added")));
        }
        this.clearAllOtherAutoArmorItems(gui, player, slot);
    }

    private boolean isArmorPiece(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }
        String typeName = item.getType().name();
        return typeName.contains("_HELMET") || typeName.contains("_CHESTPLATE") || typeName.contains("_LEGGINGS") || typeName.contains("_BOOTS") || typeName.equals("PLAYER_HEAD") || typeName.contains("SKULL") || typeName.equals("ELYTRA");
    }

    private String getArmorType(ItemStack item) {
        String typeName = item.getType().name();
        if (typeName.contains("_HELMET") || typeName.equals("PLAYER_HEAD") || typeName.contains("SKULL")) {
            return "helmet";
        }
        if (typeName.contains("_CHESTPLATE") || typeName.equals("ELYTRA")) {
            return "chestplate";
        }
        if (typeName.contains("_LEGGINGS")) {
            return "leggings";
        }
        if (typeName.contains("_BOOTS")) {
            return "boots";
        }
        return "unknown";
    }

    private void clearAllOtherAutoArmorItems(@NotNull Gui gui, @NotNull Player player, int excludeSlot) {
        ItemStack clickedItem = gui.getInventory().getItem(excludeSlot);
        if (clickedItem == null || !this.isArmorPiece(clickedItem)) {
            return;
        }
        String clickedArmorType = this.getArmorType(clickedItem);
        boolean isClickedItemAutoArmor = ItemTagUtils.hasAutoArmorTag((Plugin)this.plugin, clickedItem);
        int startSlot = this.guiConfig.getInt("editable_area.start_slot", (Integer)9);
        int endSlot = this.guiConfig.getInt("editable_area.end_slot", (Integer)44);
        for (int i = startSlot; i <= endSlot; ++i) {
            boolean hasAutoArmorTag;
            ItemStack item;
            if (i == excludeSlot || (item = gui.getInventory().getItem(i)) == null || item.getType().isAir() || !(hasAutoArmorTag = ItemTagUtils.hasAutoArmorTag((Plugin)this.plugin, item)) || !this.isArmorPiece(item)) continue;
            String itemArmorType = this.getArmorType(item);
            if (!isClickedItemAutoArmor || !clickedArmorType.equals(itemArmorType)) continue;
            ItemStack updatedItem = ItemTagUtils.removeAutoArmorTag((Plugin)this.plugin, item);
            this.removeAutoArmorLore(updatedItem);
            gui.updateItem(i, new GuiItem(updatedItem));
        }
    }

    private void addAutoArmorLore(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            ArrayList<String> lore = meta.hasLore() ? new ArrayList<String>(meta.getLore()) : new ArrayList();
            List<String> autoArmorLore = this.guiConfig.getStringList("auto_armor.lore");
            for (String loreLine : autoArmorLore) {
                lore.add(MessageProcessor.process(loreLine));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    private void removeAutoArmorLore(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasLore()) {
            ArrayList lore = new ArrayList(meta.getLore());
            List<String> configAutoArmorLore = this.guiConfig.getStringList("auto_armor.lore");
            ArrayList<String> processedAutoArmorLore = new ArrayList<String>();
            for (String loreLine : configAutoArmorLore) {
                processedAutoArmorLore.add(MessageProcessor.process(loreLine));
            }
            int autoArmorLoreSize = processedAutoArmorLore.size();
            if (lore.size() >= autoArmorLoreSize) {
                int i;
                boolean isAutoArmorLore = true;
                for (i = 0; i < autoArmorLoreSize; ++i) {
                    String expectedLine;
                    int loreIndex = lore.size() - autoArmorLoreSize + i;
                    String currentLine = (String)lore.get(loreIndex);
                    if (currentLine.equals(expectedLine = (String)processedAutoArmorLore.get(i))) continue;
                    isAutoArmorLore = false;
                    break;
                }
                if (isAutoArmorLore) {
                    for (i = 0; i < autoArmorLoreSize; ++i) {
                        lore.remove(lore.size() - 1);
                    }
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
            }
        }
    }

    private void cleanupPlayer(@NotNull Player player) {
        this.temporaryKits.remove(player.getUniqueId());
        MyScheduledTask task = this.updateTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    public void reloadGuiConfig() {
        this.loadGuiConfig();
        if (this.validateGuiConfiguration()) {
            this.plugin.getLogger().info("GUI configuration reloaded successfully!");
        } else {
            this.plugin.getLogger().warning("GUI configuration reloaded with errors - check conflicts above!");
        }
    }

    public void cleanup() {
        for (MyScheduledTask task : this.updateTasks.values()) {
            task.cancel();
        }
        this.updateTasks.clear();
        this.temporaryKits.clear();
        this.openGuis.clear();
    }
}

