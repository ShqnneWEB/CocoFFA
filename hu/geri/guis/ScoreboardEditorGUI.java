/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
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
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

public class ScoreboardEditorGUI
implements Listener {
    private final CocoFFA plugin;
    private YamlDocument guiConfig;
    private final Map<UUID, String> chatInputSessions;
    private final Map<UUID, String> chatInputTypes;
    private final Map<UUID, Arena> playerArenas;

    public ScoreboardEditorGUI(CocoFFA plugin) {
        this.plugin = plugin;
        this.chatInputSessions = new HashMap<UUID, String>();
        this.chatInputTypes = new HashMap<UUID, String>();
        this.playerArenas = new HashMap<UUID, Arena>();
        this.loadGuiConfig();
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }

    private void loadGuiConfig() {
        try {
            File guiFile = new File(this.plugin.getDataFolder(), "guis/scoreboard-gui.yml");
            this.guiConfig = YamlDocument.create(guiFile, this.plugin.getResource("guis/scoreboard-gui.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("gui-version")).setKeepAll(true).build());
        } catch (IOException e) {
            this.plugin.getLogger().severe("Failed to load scoreboard-gui.yml with BoostedYAML: " + e.getMessage());
        }
    }

    public void openScoreboardEditor(@NotNull Player player, @NotNull Arena arena) {
        this.playerArenas.put(player.getUniqueId(), arena);
        String title = this.guiConfig.getString("title", "&#FF6B35Scoreboard Editor: &#FFFF00{arena_name}").replace("{arena_name}", arena.getName());
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
    }

    private void setMenuItems(@NotNull Gui gui, @NotNull Arena arena) {
        this.addFillerItems(gui);
        this.addToggleItem(gui, arena);
        this.addItem(gui, "scoreboard-title", arena, this::handleTitleEdit);
        this.addLinesItem(gui, arena);
        this.addItem(gui, "back", arena, this::handleBack);
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
                this.plugin.getLogger().warning("Invalid filler material in scoreboard-gui.yml: " + materialName);
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

    private void addToggleItem(@NotNull Gui gui, @NotNull Arena arena) {
        GuiItem item;
        List<String> lore;
        String name;
        String materialName;
        int slot = this.guiConfig.getInt("items.scoreboard-toggle.slot", (Integer)10);
        boolean enabled = arena.isScoreboardEnabled();
        if (enabled) {
            materialName = this.guiConfig.getString("items.scoreboard-toggle.enabled-material", "GREEN_DYE");
            name = this.guiConfig.getString("items.scoreboard-toggle.enabled-name", "&#FF6B35Scoreboard Toggle");
            lore = this.guiConfig.getStringList("items.scoreboard-toggle.enabled-lore");
        } else {
            materialName = this.guiConfig.getString("items.scoreboard-toggle.material", "GRAY_DYE");
            name = this.guiConfig.getString("items.scoreboard-toggle.name", "&#FF6B35Scoreboard Toggle");
            lore = this.guiConfig.getStringList("items.scoreboard-toggle.lore");
        }
        ArrayList<String> coloredLore = new ArrayList<String>();
        for (String line : lore) {
            coloredLore.add(MessageProcessor.process(line));
        }
        if (SkullUtils.isBase64Material(materialName)) {
            ItemStack skull = SkullUtils.createSkullFromBase64(materialName);
            item = new GuiItem(skull, event -> {
                Player player = (Player)event.getWhoClicked();
                Arena playerArena = this.playerArenas.get(player.getUniqueId());
                if (playerArena != null) {
                    this.toggleScoreboard(player, playerArena);
                }
            });
        } else {
            try {
                Material material = Material.valueOf((String)materialName.toUpperCase());
                item = ItemBuilder.from(material).asGuiItem(event -> {
                    Player player = (Player)event.getWhoClicked();
                    Arena playerArena = this.playerArenas.get(player.getUniqueId());
                    if (playerArena != null) {
                        this.toggleScoreboard(player, playerArena);
                    }
                });
            } catch (IllegalArgumentException e) {
                this.plugin.getLogger().warning("Invalid toggle material in scoreboard-gui.yml: " + materialName);
                item = ItemBuilder.from(Material.GRAY_DYE).asGuiItem(event -> {
                    Player player = (Player)event.getWhoClicked();
                    Arena playerArena = this.playerArenas.get(player.getUniqueId());
                    if (playerArena != null) {
                        this.toggleScoreboard(player, playerArena);
                    }
                });
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

    private void addItem(@NotNull Gui gui, @NotNull String configPath, @NotNull Arena arena, ItemClickHandler handler) {
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
                    handler.handle(player, playerArena, event.getClick());
                }
            });
        } else {
            try {
                Material material = Material.valueOf((String)materialName.toUpperCase());
                item = ItemBuilder.from(material).asGuiItem(event -> {
                    Player player = (Player)event.getWhoClicked();
                    Arena playerArena = this.playerArenas.get(player.getUniqueId());
                    if (playerArena != null) {
                        handler.handle(player, playerArena, event.getClick());
                    }
                });
            } catch (IllegalArgumentException e) {
                this.plugin.getLogger().warning("Invalid material in scoreboard-gui.yml at " + configPath + ": " + materialName);
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

    private void addLinesItem(@NotNull Gui gui, @NotNull Arena arena) {
        GuiItem item;
        String configPath = "scoreboard-lines";
        int slot = this.guiConfig.getInt("items." + configPath + ".slot", (Integer)14);
        String materialName = this.guiConfig.getString("items." + configPath + ".material", "PAPER");
        String name = this.guiConfig.getString("items." + configPath + ".name", "Scoreboard Lines");
        name = this.replacePlaceholders(name, arena);
        ArrayList<String> lore = new ArrayList<String>(this.guiConfig.getStringList("items." + configPath + ".lore"));
        for (int i = 0; i < lore.size(); ++i) {
            String line = (String)lore.get(i);
            if (!line.contains("{scoreboard_lines}")) continue;
            lore.remove(i);
            List<String> scoreboardLines = arena.getScoreboardLines();
            if (scoreboardLines.isEmpty()) {
                String noLinesText = this.guiConfig.getString("scoreboard-lines.no-lines-text", " &7No lines configured");
                lore.add(i, noLinesText);
                break;
            }
            String lineFormat = this.guiConfig.getString("scoreboard-lines.line-format", " &f{number}. &7{line}");
            for (int j = 0; j < scoreboardLines.size(); ++j) {
                String formattedLine = lineFormat.replace("{number}", String.valueOf(j + 1)).replace("{line}", scoreboardLines.get(j));
                lore.add(i + j, formattedLine);
            }
            break;
        }
        ArrayList<TextComponent> coloredLore = new ArrayList<TextComponent>();
        for (String line : lore) {
            line = this.replacePlaceholders(line, arena);
            coloredLore.add(Component.text(MessageProcessor.process(line)));
        }
        if (SkullUtils.isBase64Material(materialName)) {
            ItemStack skull = SkullUtils.createSkullFromBase64(materialName);
            item = new GuiItem(skull, event -> {
                Player player = (Player)event.getWhoClicked();
                Arena playerArena = this.playerArenas.get(player.getUniqueId());
                if (playerArena != null) {
                    this.handleLinesClick(player, playerArena, event.getClick());
                }
            });
        } else {
            try {
                Material material = Material.valueOf((String)materialName.toUpperCase());
                item = ItemBuilder.from(material).asGuiItem(event -> {
                    Player player = (Player)event.getWhoClicked();
                    Arena playerArena = this.playerArenas.get(player.getUniqueId());
                    if (playerArena != null) {
                        this.handleLinesClick(player, playerArena, event.getClick());
                    }
                });
            } catch (IllegalArgumentException e) {
                this.plugin.getLogger().warning("Invalid material for scoreboard-lines in scoreboard-gui.yml: " + materialName);
                item = ItemBuilder.from(Material.PAPER).asGuiItem(event -> {
                    Player player = (Player)event.getWhoClicked();
                    Arena playerArena = this.playerArenas.get(player.getUniqueId());
                    if (playerArena != null) {
                        this.handleLinesClick(player, playerArena, event.getClick());
                    }
                });
            }
        }
        ItemStack itemStack = item.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(MessageProcessor.process(name));
            ArrayList<String> coloredLoreStrings = new ArrayList<String>();
            for (Component component : coloredLore) {
                coloredLoreStrings.add(LegacyComponentSerializer.legacyAmpersand().serialize(component));
            }
            itemMeta.setLore(coloredLoreStrings);
            itemStack.setItemMeta(itemMeta);
        }
        gui.setItem(slot, item);
    }

    private String replacePlaceholders(@NotNull String text, @NotNull Arena arena) {
        text = text.replace("{arena_name}", arena.getName());
        text = text.replace("{scoreboard_title}", arena.getScoreboardTitle());
        return text;
    }

    private void handleTitleEdit(Player player, Arena arena, ClickType clickType) {
        this.startTitleEdit(player, arena);
    }

    private void handleBack(Player player, Arena arena, ClickType clickType) {
        this.closeEditor(player, arena);
    }

    private void handleLinesClick(@NotNull Player player, @NotNull Arena arena, @NotNull ClickType clickType) {
        switch (clickType) {
            case LEFT: {
                this.startLineAdd(player, arena);
                break;
            }
            case RIGHT: {
                this.removeLastLine(player, arena);
                break;
            }
            case SHIFT_RIGHT: {
                this.clearAllLines(player, arena);
            }
        }
    }

    private void closeEditor(@NotNull Player player, @NotNull Arena arena) {
        this.playerArenas.remove(player.getUniqueId());
        new MainEditorGUI(this.plugin).openMainEditor(player, arena);
    }

    private void toggleScoreboard(@NotNull Player player, @NotNull Arena arena) {
        boolean newState = !arena.isScoreboardEnabled();
        arena.setScoreboardEnabled(newState);
        this.plugin.getArenaManager().saveArena(arena);
        String messageKey = newState ? "editor.scoreboard.toggle.enabled" : "editor.scoreboard.toggle.disabled";
        player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage(messageKey, "{arena_name}", arena.getName())));
        this.openScoreboardEditor(player, arena);
    }

    private void startTitleEdit(@NotNull Player player, @NotNull Arena arena) {
        player.closeInventory();
        this.chatInputSessions.put(player.getUniqueId(), arena.getName());
        this.chatInputTypes.put(player.getUniqueId(), "scoreboard-title");
        player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.scoreboard.title.prompt")));
    }

    private void startLineAdd(@NotNull Player player, @NotNull Arena arena) {
        player.closeInventory();
        this.chatInputSessions.put(player.getUniqueId(), arena.getName());
        this.chatInputTypes.put(player.getUniqueId(), "scoreboard-line-add");
        player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.scoreboard.lines.add-prompt")));
    }

    private void removeLastLine(@NotNull Player player, @NotNull Arena arena) {
        ArrayList<String> lines = new ArrayList<String>(arena.getScoreboardLines());
        if (!lines.isEmpty()) {
            lines.remove(lines.size() - 1);
            arena.setScoreboardLines(lines);
            this.plugin.getArenaManager().saveArena(arena);
            player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.scoreboard.lines.removed", "{arena_name}", arena.getName())));
        }
        this.openScoreboardEditor(player, arena);
    }

    private void clearAllLines(@NotNull Player player, @NotNull Arena arena) {
        arena.setScoreboardLines(new ArrayList<String>());
        this.plugin.getArenaManager().saveArena(arena);
        player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.scoreboard.lines.cleared", "{arena_name}", arena.getName())));
        this.openScoreboardEditor(player, arena);
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
                if ("scoreboard-title".equals(inputType)) {
                    player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.scoreboard.title.cancelled")));
                } else if ("scoreboard-line-add".equals(inputType)) {
                    player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.scoreboard.lines.cancelled")));
                }
                this.openScoreboardEditor(player, arena);
            });
            return;
        }
        this.plugin.getUniversalScheduler().runTask((Entity)player, () -> this.handleChatInput(player, arena, inputType, message));
    }

    private void handleChatInput(@NotNull Player player, @NotNull Arena arena, @NotNull String inputType, @NotNull String message) {
        UUID playerId = player.getUniqueId();
        switch (inputType) {
            case "scoreboard-title": {
                arena.setScoreboardTitle(message);
                this.plugin.getArenaManager().saveArena(arena);
                player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.scoreboard.title.updated", "{arena_name}", arena.getName())));
                break;
            }
            case "scoreboard-line-add": {
                ArrayList<String> lines = new ArrayList<String>(arena.getScoreboardLines());
                lines.add(message);
                arena.setScoreboardLines(lines);
                this.plugin.getArenaManager().saveArena(arena);
                player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.scoreboard.lines.added", "{arena_name}", arena.getName())));
            }
        }
        this.chatInputSessions.remove(playerId);
        this.chatInputTypes.remove(playerId);
        this.openScoreboardEditor(player, arena);
    }

    public void reloadGuiConfig() {
        this.loadGuiConfig();
    }

    @FunctionalInterface
    private static interface ItemClickHandler {
        public void handle(Player var1, Arena var2, ClickType var3);
    }
}

