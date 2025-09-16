/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Sound
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package hu.geri.guis;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.libs.boostedyaml.YamlDocument;
import hu.geri.libs.boostedyaml.dvs.versioning.BasicVersioning;
import hu.geri.libs.boostedyaml.settings.dumper.DumperSettings;
import hu.geri.libs.boostedyaml.settings.general.GeneralSettings;
import hu.geri.libs.boostedyaml.settings.loader.LoaderSettings;
import hu.geri.libs.boostedyaml.settings.updater.UpdaterSettings;
import hu.geri.libs.gui.builder.gui.PaginatedBuilder;
import hu.geri.libs.gui.builder.item.ItemBuilder;
import hu.geri.libs.gui.guis.Gui;
import hu.geri.libs.gui.guis.GuiItem;
import hu.geri.libs.gui.guis.PaginatedGui;
import hu.geri.managers.VanishManager;
import hu.geri.processor.MessageProcessor;
import hu.geri.utils.SkullUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class SpectatorPlayerSelectorGUI {
    private final CocoFFA plugin;
    private YamlDocument guiConfig;

    public SpectatorPlayerSelectorGUI(CocoFFA plugin) {
        this.plugin = plugin;
        this.loadGUIConfig();
    }

    private void loadGUIConfig() {
        try {
            File configFile = new File(this.plugin.getDataFolder(), "guis/spectator-gui.yml");
            if (!configFile.exists()) {
                this.plugin.saveResource("guis/spectator-gui.yml", false);
            }
            this.guiConfig = YamlDocument.create(configFile, this.plugin.getResource("guis/spectator-gui.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("gui-version")).setKeepAll(true).build());
        } catch (IOException e) {
            this.plugin.getLogger().severe("Failed to load spectator-gui.yml: " + e.getMessage());
        }
    }

    public void reloadGUIConfig() {
        try {
            this.guiConfig.reload();
        } catch (IOException e) {
            this.plugin.getLogger().severe("Failed to reload spectator-gui.yml: " + e.getMessage());
        }
    }

    public void openPlayerSelector(Player viewer) {
        String arenaName = this.plugin.getSpectatorManager().getSpectatingArena(viewer);
        if (arenaName == null) {
            viewer.sendMessage(this.plugin.getLocaleManager().getMessage("commands.spectate.not-spectating"));
            return;
        }
        Arena arena = this.plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            viewer.sendMessage(this.plugin.getLocaleManager().getMessage("commands.spectate.spectated-arena-not-found"));
            return;
        }
        String titleText = this.guiConfig.getString("title", "&ePlayer Selector - {arena_name}").replace("{arena_name}", arena.getDisplayName());
        TextComponent title = LegacyComponentSerializer.legacyAmpersand().deserialize(titleText);
        int rows = this.guiConfig.getInt("rows", (Integer)6);
        if (rows < 1 || rows > 6) {
            rows = 6;
        }
        List<Integer> playerSlots = this.guiConfig.getIntList("player-slots");
        PaginatedGui gui = ((PaginatedBuilder)((PaginatedBuilder)Gui.paginated().title(title)).rows(rows)).pageSize(playerSlots.size()).create();
        for (Player arenaPlayer : arena.getPlayers()) {
            if (VanishManager.isVanished(arenaPlayer) || !arenaPlayer.isOnline()) continue;
            GuiItem playerItem = this.createPlayerItem(arenaPlayer, viewer);
            gui.addItem(playerItem);
        }
        if (this.guiConfig.getBoolean("navigation.previous-page.enabled", (Boolean)true).booleanValue()) {
            int prevSlot = this.guiConfig.getInt("navigation.previous-page.slot", (Integer)48);
            gui.setItem(prevSlot / 9 + 1, prevSlot % 9 + 1, this.createPreviousPageItem(gui));
        }
        if (this.guiConfig.getBoolean("navigation.close.enabled", (Boolean)true).booleanValue()) {
            int closeSlot = this.guiConfig.getInt("navigation.close.slot", (Integer)49);
            gui.setItem(closeSlot / 9 + 1, closeSlot % 9 + 1, this.createCloseItem(viewer));
        }
        if (this.guiConfig.getBoolean("navigation.next-page.enabled", (Boolean)true).booleanValue()) {
            int nextSlot = this.guiConfig.getInt("navigation.next-page.slot", (Integer)50);
            gui.setItem(nextSlot / 9 + 1, nextSlot % 9 + 1, this.createNextPageItem(gui));
        }
        this.addFillerItems(gui);
        gui.open((HumanEntity)viewer);
        String openSound = this.guiConfig.getString("open-sound", "");
        if (!openSound.isEmpty()) {
            try {
                Sound sound = Sound.valueOf((String)openSound.toUpperCase());
                viewer.playSound(viewer.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
    }

    private GuiItem createPlayerItem(Player target, Player viewer) {
        GuiItem item;
        String materialName = this.guiConfig.getString("player-item.material", "PLAYER_HEAD");
        String arenaName = this.plugin.getSpectatorManager().getSpectatingArena(viewer);
        Arena arena = this.plugin.getArenaManager().getArena(arenaName);
        String arenaDisplayName = arena != null ? arena.getDisplayName() : arenaName;
        String name = this.guiConfig.getString("player-item.name", "&#FFEE00{player_name}").replace("{player_name}", target.getName());
        ArrayList<String> lore = new ArrayList<String>();
        for (String line : this.guiConfig.getStringList("player-item.lore")) {
            lore.add(line.replace("{player_name}", target.getName()).replace("{arena_name}", arenaDisplayName));
        }
        if (SkullUtils.isBase64Material(materialName)) {
            ItemStack skull = SkullUtils.createSkullFromBase64(materialName);
            item = new GuiItem(skull, event -> {
                event.setCancelled(true);
                if (target.isOnline()) {
                    viewer.teleport(target.getLocation());
                    viewer.sendMessage(this.plugin.getLocaleManager().getMessage("commands.spectate.teleported-to-player", "{player_name}", target.getName()));
                    viewer.closeInventory();
                } else {
                    viewer.sendMessage(this.plugin.getLocaleManager().getMessage("commands.spectate.player-offline", "{player_name}", target.getName()));
                }
            });
        } else if (materialName.equalsIgnoreCase("PLAYER_HEAD")) {
            item = ItemBuilder.skull().owner((OfflinePlayer)target).asGuiItem(event -> {
                event.setCancelled(true);
                if (target.isOnline()) {
                    viewer.teleport(target.getLocation());
                    viewer.sendMessage(this.plugin.getLocaleManager().getMessage("commands.spectate.teleported-to-player", "{player_name}", target.getName()));
                    viewer.closeInventory();
                } else {
                    viewer.sendMessage(this.plugin.getLocaleManager().getMessage("commands.spectate.player-offline", "{player_name}", target.getName()));
                }
            });
        } else {
            try {
                Material material = Material.valueOf((String)materialName.toUpperCase());
                item = ItemBuilder.from(material).asGuiItem(event -> {
                    event.setCancelled(true);
                    if (target.isOnline()) {
                        viewer.teleport(target.getLocation());
                        viewer.sendMessage(this.plugin.getLocaleManager().getMessage("commands.spectate.teleported-to-player", "{player_name}", target.getName()));
                        viewer.closeInventory();
                    } else {
                        viewer.sendMessage(this.plugin.getLocaleManager().getMessage("commands.spectate.player-offline", "{player_name}", target.getName()));
                    }
                });
            } catch (IllegalArgumentException e) {
                item = ItemBuilder.skull().owner((OfflinePlayer)target).asGuiItem(event -> {
                    event.setCancelled(true);
                    if (target.isOnline()) {
                        viewer.teleport(target.getLocation());
                        viewer.sendMessage(this.plugin.getLocaleManager().getMessage("commands.spectate.teleported-to-player", "{player_name}", target.getName()));
                        viewer.closeInventory();
                    } else {
                        viewer.sendMessage(this.plugin.getLocaleManager().getMessage("commands.spectate.player-offline", "{player_name}", target.getName()));
                    }
                });
            }
        }
        ItemStack itemStack = item.getItemStack();
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
        return item;
    }

    private GuiItem createPreviousPageItem(PaginatedGui gui) {
        GuiItem item;
        String materialName = this.guiConfig.getString("navigation.previous-page.material", "ARROW");
        if (SkullUtils.isBase64Material(materialName)) {
            ItemStack skull = SkullUtils.createSkullFromBase64(materialName);
            item = new GuiItem(skull, event -> {
                event.setCancelled(true);
                gui.previous();
            });
        } else {
            try {
                Material material = Material.valueOf((String)materialName.toUpperCase());
                item = ItemBuilder.from(material).asGuiItem(event -> {
                    event.setCancelled(true);
                    gui.previous();
                });
            } catch (IllegalArgumentException e) {
                item = ItemBuilder.from(Material.ARROW).asGuiItem(event -> {
                    event.setCancelled(true);
                    gui.previous();
                });
            }
        }
        ItemStack itemStack = item.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(MessageProcessor.process(this.guiConfig.getString("navigation.previous-page.name", "&ePrevious Page")));
            ArrayList<String> processedLore = new ArrayList<String>();
            for (String line : this.guiConfig.getStringList("navigation.previous-page.lore")) {
                processedLore.add(MessageProcessor.process(line));
            }
            itemMeta.setLore(processedLore);
            itemStack.setItemMeta(itemMeta);
        }
        return item;
    }

    private GuiItem createNextPageItem(PaginatedGui gui) {
        GuiItem item;
        String materialName = this.guiConfig.getString("navigation.next-page.material", "ARROW");
        if (SkullUtils.isBase64Material(materialName)) {
            ItemStack skull = SkullUtils.createSkullFromBase64(materialName);
            item = new GuiItem(skull, event -> {
                event.setCancelled(true);
                gui.next();
            });
        } else {
            try {
                Material material = Material.valueOf((String)materialName.toUpperCase());
                item = ItemBuilder.from(material).asGuiItem(event -> {
                    event.setCancelled(true);
                    gui.next();
                });
            } catch (IllegalArgumentException e) {
                item = ItemBuilder.from(Material.ARROW).asGuiItem(event -> {
                    event.setCancelled(true);
                    gui.next();
                });
            }
        }
        ItemStack itemStack = item.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(MessageProcessor.process(this.guiConfig.getString("navigation.next-page.name", "&eNext Page")));
            ArrayList<String> processedLore = new ArrayList<String>();
            for (String line : this.guiConfig.getStringList("navigation.next-page.lore")) {
                processedLore.add(MessageProcessor.process(line));
            }
            itemMeta.setLore(processedLore);
            itemStack.setItemMeta(itemMeta);
        }
        return item;
    }

    private GuiItem createCloseItem(Player viewer) {
        GuiItem item;
        String materialName = this.guiConfig.getString("navigation.close.material", "BARRIER");
        if (SkullUtils.isBase64Material(materialName)) {
            ItemStack skull = SkullUtils.createSkullFromBase64(materialName);
            item = new GuiItem(skull, event -> {
                event.setCancelled(true);
                viewer.closeInventory();
            });
        } else {
            try {
                Material material = Material.valueOf((String)materialName.toUpperCase());
                item = ItemBuilder.from(material).asGuiItem(event -> {
                    event.setCancelled(true);
                    viewer.closeInventory();
                });
            } catch (IllegalArgumentException e) {
                item = ItemBuilder.from(Material.BARRIER).asGuiItem(event -> {
                    event.setCancelled(true);
                    viewer.closeInventory();
                });
            }
        }
        ItemStack itemStack = item.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(MessageProcessor.process(this.guiConfig.getString("navigation.close.name", "&cClose Menu")));
            ArrayList<String> processedLore = new ArrayList<String>();
            for (String line : this.guiConfig.getStringList("navigation.close.lore")) {
                processedLore.add(MessageProcessor.process(line));
            }
            itemMeta.setLore(processedLore);
            itemStack.setItemMeta(itemMeta);
        }
        return item;
    }

    private void addFillerItems(@NotNull PaginatedGui gui) {
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
                this.plugin.getLogger().warning("Invalid filler material in spectator-gui.yml: " + materialName);
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
        int rows = this.guiConfig.getInt("rows", (Integer)6);
        int totalSlots = rows * 9;
        for (int slot : fillerSlots) {
            if (slot < 0 || slot >= totalSlots) continue;
            gui.setItem(slot / 9 + 1, slot % 9 + 1, filler);
        }
    }
}

