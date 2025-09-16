/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.guis;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.guis.RewardEditorGUI;
import hu.geri.guis.ScoreboardEditorGUI;
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
import hu.geri.processor.MessageProcessor;
import hu.geri.utils.SkullUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class MainEditorGUI
implements Listener {
    private final CocoFFA plugin;
    private YamlDocument guiConfig;
    private final Map<UUID, String> chatInputSessions;
    private final Map<UUID, String> chatInputTypes;
    private final Map<UUID, Arena> playerArenas;

    public MainEditorGUI(CocoFFA plugin) {
        this.plugin = plugin;
        this.chatInputSessions = new HashMap<UUID, String>();
        this.chatInputTypes = new HashMap<UUID, String>();
        this.playerArenas = new HashMap<UUID, Arena>();
        this.loadGuiConfig();
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }

    private void loadGuiConfig() {
        try {
            File guiFile = new File(this.plugin.getDataFolder(), "guis/main-gui.yml");
            this.guiConfig = YamlDocument.create(guiFile, this.plugin.getResource("guis/main-gui.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("gui-version")).setKeepAll(true).build());
        } catch (IOException e) {
            this.plugin.getLogger().severe("Failed to load main-gui.yml with BoostedYAML: " + e.getMessage());
        }
    }

    public void openMainEditor(Player player, Arena arena) {
        this.playerArenas.put(player.getUniqueId(), arena);
        String title = this.guiConfig.getString("title", "&#FF6B35Editor: &#FFFF00{arena_name}").replace("{arena_name}", arena.getName());
        title = MessageProcessor.process(title);
        int rows = this.guiConfig.getInt("rows", (Integer)6);
        Gui gui = ((SimpleBuilder)((SimpleBuilder)((SimpleBuilder)Gui.gui().title(Component.text(title))).rows(rows)).disableAllInteractions()).create();
        this.setMenuItems(gui, arena);
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
        player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.opened", "{arena_name}", arena.getName())));
    }

    private void setMenuItems(@NotNull Gui gui, @NotNull Arena arena) {
        this.addFillerItems(gui);
        this.addItem(gui, "display-name-editor", arena, this::handleDisplayNameEditor);
        this.addItem(gui, "kit-editor", arena, this::handleKitEditor);
        this.addLocationItem(gui, "join-location", arena, (player, clickType) -> this.handleLocationSetting(player, arena, "join"));
        this.addLocationItem(gui, "exit-location", arena, (player, clickType) -> this.handleLocationSetting(player, arena, "exit"));
        this.addLocationItem(gui, "border-center", arena, (player, clickType) -> this.handleLocationSetting(player, arena, "border"));
        this.addItem(gui, "scoreboard-editor", arena, this::handleScoreboardEditor);
        this.addItem(gui, "reward-editor", arena, this::handleRewardEditor);
        this.addItem(gui, "delete-arena", arena, this::handleDeleteArena);
        this.addItem(gui, "exit", arena, this::handleExit);
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
                this.plugin.getLogger().warning("Invalid filler material in main-gui.yml: " + materialName);
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

    private void addItem(Gui gui, String configPath, Arena arena, ItemClickHandler handler) {
        GuiItem item;
        int slot = this.guiConfig.getInt("items." + configPath + ".slot", (Integer)-1);
        if (slot == -1) {
            return;
        }
        String materialName = this.guiConfig.getString("items." + configPath + ".material", "STONE");
        String name = this.guiConfig.getString("items." + configPath + ".name", "Item");
        name = this.replacePlaceholders(name, arena);
        List<String> lore = this.guiConfig.getStringList("items." + configPath + ".lore");
        ArrayList<String> coloredLore = new ArrayList<String>();
        for (String line : lore) {
            line = this.replacePlaceholders(line, arena);
            coloredLore.add(MessageProcessor.process(line));
        }
        if (SkullUtils.isBase64Material(materialName)) {
            ItemStack skull = SkullUtils.createSkullFromBase64(materialName);
            item = new GuiItem(skull, event -> {
                Player player = (Player)event.getWhoClicked();
                Arena playerArena = this.playerArenas.get(player.getUniqueId());
                if (playerArena != null) {
                    handler.handle(player, event.getClick());
                }
            });
        } else {
            try {
                Material material = Material.valueOf((String)materialName.toUpperCase());
                item = ItemBuilder.from(material).asGuiItem(event -> {
                    Player player = (Player)event.getWhoClicked();
                    Arena playerArena = this.playerArenas.get(player.getUniqueId());
                    if (playerArena != null) {
                        handler.handle(player, event.getClick());
                    }
                });
            } catch (IllegalArgumentException e) {
                this.plugin.getLogger().warning("Invalid material in main-gui.yml at " + configPath + ": " + materialName);
                return;
            }
        }
        ItemStack itemStack = item.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(MessageProcessor.process(name));
            itemMeta.setLore(coloredLore);
            itemStack.setItemMeta(itemMeta);
        }
        gui.setItem(slot, item);
    }

    private void addLocationItem(Gui gui, String configPath, Arena arena, ItemClickHandler handler) {
        GuiItem item;
        int slot = this.guiConfig.getInt("items." + configPath + ".slot", (Integer)-1);
        if (slot == -1) {
            return;
        }
        String materialName = this.guiConfig.getString("items." + configPath + ".material", "PLAYER_HEAD");
        String name = this.guiConfig.getString("items." + configPath + ".name", "Location");
        name = this.replacePlaceholders(name, arena);
        List<String> lore = this.guiConfig.getStringList("items." + configPath + ".lore");
        ArrayList<String> coloredLore = new ArrayList<String>();
        for (String line : lore) {
            line = this.replacePlaceholders(line, arena);
            coloredLore.add(MessageProcessor.process(line));
        }
        if (SkullUtils.isBase64Material(materialName)) {
            ItemStack skull = SkullUtils.createSkullFromBase64(materialName);
            item = new GuiItem(skull, event -> {
                Player player = (Player)event.getWhoClicked();
                Arena playerArena = this.playerArenas.get(player.getUniqueId());
                if (playerArena != null) {
                    handler.handle(player, event.getClick());
                }
            });
        } else {
            try {
                Material material = Material.valueOf((String)materialName.toUpperCase());
                item = ItemBuilder.from(material).asGuiItem(event -> {
                    Player player = (Player)event.getWhoClicked();
                    Arena playerArena = this.playerArenas.get(player.getUniqueId());
                    if (playerArena != null) {
                        handler.handle(player, event.getClick());
                    }
                });
            } catch (IllegalArgumentException e) {
                this.plugin.getLogger().warning("Invalid material in main-gui.yml at " + configPath + ": " + materialName);
                return;
            }
        }
        ItemStack itemStack = item.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(MessageProcessor.process(name));
            itemMeta.setLore(coloredLore);
            itemStack.setItemMeta(itemMeta);
        }
        gui.setItem(slot, item);
    }

    private String replacePlaceholders(String text, Arena arena) {
        text = text.replace("{arena_name}", arena.getName());
        text = text.replace("{display_name}", arena.getDisplayName());
        text = text.replace("{min_players}", String.valueOf(arena.getMinPlayers()));
        text = text.replace("{max_players}", String.valueOf(arena.getMaxPlayers()));
        text = text.replace("{max_players_display}", arena.getMaxPlayers() == -1 ? this.plugin.getConfigManager().getInfiniteSymbol() : String.valueOf(arena.getMaxPlayers()));
        Location joinLoc = arena.getStartLocation();
        if (joinLoc != null && joinLoc.getWorld() != null) {
            text = text.replace("{join_world}", joinLoc.getWorld().getName());
            text = text.replace("{join_x}", String.valueOf(joinLoc.getBlockX()));
            text = text.replace("{join_y}", String.valueOf(joinLoc.getBlockY()));
            text = text.replace("{join_z}", String.valueOf(joinLoc.getBlockZ()));
        } else {
            text = text.replace("{join_world}", "---");
            text = text.replace("{join_x}", "---");
            text = text.replace("{join_y}", "---");
            text = text.replace("{join_z}", "---");
        }
        Location exitLoc = arena.getExitLocation();
        if (exitLoc != null && exitLoc.getWorld() != null) {
            text = text.replace("{exit_world}", exitLoc.getWorld().getName());
            text = text.replace("{exit_x}", String.valueOf(exitLoc.getBlockX()));
            text = text.replace("{exit_y}", String.valueOf(exitLoc.getBlockY()));
            text = text.replace("{exit_z}", String.valueOf(exitLoc.getBlockZ()));
        } else {
            text = text.replace("{exit_world}", "---");
            text = text.replace("{exit_x}", "---");
            text = text.replace("{exit_y}", "---");
            text = text.replace("{exit_z}", "---");
        }
        Location borderLoc = arena.getBorderCenterLocation();
        if (borderLoc != null && borderLoc.getWorld() != null) {
            text = text.replace("{border_world}", borderLoc.getWorld().getName());
            text = text.replace("{border_x}", String.valueOf(borderLoc.getBlockX()));
            text = text.replace("{border_y}", String.valueOf(borderLoc.getBlockY()));
            text = text.replace("{border_z}", String.valueOf(borderLoc.getBlockZ()));
        } else {
            text = text.replace("{border_world}", "---");
            text = text.replace("{border_x}", "---");
            text = text.replace("{border_y}", "---");
            text = text.replace("{border_z}", "---");
        }
        return text;
    }

    private void handleDisplayNameEditor(Player player, ClickType clickType) {
        Arena arena = this.playerArenas.get(player.getUniqueId());
        if (arena == null) {
            return;
        }
        this.startDisplayNameEdit(player, arena);
    }

    private void handleKitEditor(Player player, ClickType clickType) {
        Arena arena = this.playerArenas.get(player.getUniqueId());
        if (arena == null) {
            return;
        }
        this.openKitEditor(player, arena);
    }

    private void handleLocationSetting(Player player, Arena arena, String locationType) {
        this.startLocationSetting(player, arena, locationType);
    }

    private void handleScoreboardEditor(Player player, ClickType clickType) {
        Arena arena = this.playerArenas.get(player.getUniqueId());
        if (arena == null) {
            return;
        }
        this.openScoreboardEditor(player, arena);
    }

    private void handleRewardEditor(Player player, ClickType clickType) {
        Arena arena = this.playerArenas.get(player.getUniqueId());
        if (arena == null) {
            return;
        }
        this.openRewardEditor(player, arena);
    }

    private void handleDeleteArena(Player player, ClickType clickType) {
        Arena arena = this.playerArenas.get(player.getUniqueId());
        if (arena == null) {
            return;
        }
        if (clickType == ClickType.SHIFT_LEFT) {
            this.deleteArena(player, arena);
        }
    }

    private void handleExit(Player player, ClickType clickType) {
        this.closeEditor(player);
    }

    private void closeEditor(Player player) {
        player.closeInventory();
        this.playerArenas.remove(player.getUniqueId());
    }

    private void startDisplayNameEdit(Player player, Arena arena) {
        player.closeInventory();
        this.chatInputSessions.put(player.getUniqueId(), arena.getName());
        this.chatInputTypes.put(player.getUniqueId(), "display-name");
        player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.display-name.prompt", "{arena_name}", arena.getName())));
    }

    private void openKitEditor(Player player, Arena arena) {
        this.playerArenas.remove(player.getUniqueId());
        this.plugin.getKitEditorGUI().openKitEditor(player, arena.getName());
    }

    private void startLocationSetting(Player player, Arena arena, String locationType) {
        player.closeInventory();
        this.chatInputSessions.put(player.getUniqueId(), arena.getName());
        this.chatInputTypes.put(player.getUniqueId(), "location-" + locationType);
        player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.location." + locationType + ".prompt")));
    }

    private void openScoreboardEditor(Player player, Arena arena) {
        this.playerArenas.remove(player.getUniqueId());
        new ScoreboardEditorGUI(this.plugin).openScoreboardEditor(player, arena);
    }

    private void openRewardEditor(Player player, Arena arena) {
        this.playerArenas.remove(player.getUniqueId());
        new RewardEditorGUI(this.plugin).openRewardEditor(player, arena);
    }

    private void deleteArena(Player player, Arena arena) {
        player.closeInventory();
        this.playerArenas.remove(player.getUniqueId());
        boolean success = this.plugin.getArenaManager().deleteArena(arena.getName());
        if (success) {
            player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("commands.delete.success", "{arena_name}", arena.getName())));
        } else {
            player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("commands.delete.failed", "{arena_name}", arena.getName())));
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        if (!this.chatInputSessions.containsKey(playerId)) {
            return;
        }
        event.setCancelled(true);
        String message = event.getMessage();
        String arenaName = this.chatInputSessions.get(playerId);
        String inputType = this.chatInputTypes.get(playerId);
        Arena arena = this.plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            this.chatInputSessions.remove(playerId);
            this.chatInputTypes.remove(playerId);
            return;
        }
        if ("stop".equalsIgnoreCase(message)) {
            this.chatInputSessions.remove(playerId);
            this.chatInputTypes.remove(playerId);
            this.plugin.getUniversalScheduler().runTask((Entity)player, () -> {
                if (inputType.startsWith("location-")) {
                    String locType = inputType.substring(9);
                    player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.location." + locType + ".cancelled")));
                } else if ("display-name".equals(inputType)) {
                    player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.display-name.cancelled")));
                }
                this.openMainEditor(player, arena);
            });
            return;
        }
        this.plugin.getUniversalScheduler().runTask((Entity)player, () -> this.handleChatInput(player, arena, inputType, message));
    }

    private void handleChatInput(Player player, Arena arena, String inputType, String message) {
        UUID playerId = player.getUniqueId();
        switch (inputType) {
            case "display-name": {
                arena.setDisplayName(message);
                this.plugin.getArenaManager().saveArena(arena);
                player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.display-name.updated", "{arena_name}", arena.getName())));
                break;
            }
            case "location-join": {
                if ("DONE".equalsIgnoreCase(message)) {
                    arena.setStartLocation(player.getLocation());
                    this.plugin.getArenaManager().saveArena(arena);
                    player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.location.join.set", "{arena_name}", arena.getName())));
                    break;
                }
                if ("CANCEL".equalsIgnoreCase(message)) {
                    player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.location.join.cancelled")));
                    break;
                }
                player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.location.join.prompt")));
                return;
            }
            case "location-exit": {
                if ("DONE".equalsIgnoreCase(message)) {
                    arena.setExitLocation(player.getLocation());
                    this.plugin.getArenaManager().saveArena(arena);
                    player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.location.exit.set", "{arena_name}", arena.getName())));
                    break;
                }
                if ("CANCEL".equalsIgnoreCase(message)) {
                    player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.location.exit.cancelled")));
                    break;
                }
                player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.location.exit.prompt")));
                return;
            }
            case "location-border": {
                if ("DONE".equalsIgnoreCase(message)) {
                    arena.setBorderCenterLocation(player.getLocation());
                    this.plugin.getArenaManager().saveArena(arena);
                    player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.location.border.set", "{arena_name}", arena.getName())));
                    break;
                }
                if ("CANCEL".equalsIgnoreCase(message)) {
                    player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.location.border.cancelled")));
                    break;
                }
                player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.location.border.prompt")));
                return;
            }
        }
        this.chatInputSessions.remove(playerId);
        this.chatInputTypes.remove(playerId);
        this.openMainEditor(player, arena);
    }

    public void reloadGuiConfig() {
        this.loadGuiConfig();
    }

    @FunctionalInterface
    private static interface ItemClickHandler {
        public void handle(Player var1, ClickType var2);
    }
}

