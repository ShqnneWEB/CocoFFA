/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
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
import hu.geri.libs.gui.components.GuiAction;
import hu.geri.libs.gui.guis.Gui;
import hu.geri.libs.gui.guis.GuiItem;
import hu.geri.libs.gui.guis.PaginatedGui;
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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class RewardEditorGUI
implements Listener {
    private final CocoFFA plugin;
    private YamlDocument guiConfig;
    private final Map<UUID, String> chatInputSessions;
    private final Map<UUID, Arena> playerArenas;

    public RewardEditorGUI(CocoFFA plugin) {
        this.plugin = plugin;
        this.chatInputSessions = new HashMap<UUID, String>();
        this.playerArenas = new HashMap<UUID, Arena>();
        this.loadGuiConfig();
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }

    private void loadGuiConfig() {
        try {
            File guiFile = new File(this.plugin.getDataFolder(), "guis/reward-editor-gui.yml");
            this.guiConfig = YamlDocument.create(guiFile, this.plugin.getResource("guis/reward-editor-gui.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("gui-version")).setKeepAll(true).build());
        } catch (IOException e) {
            this.plugin.getLogger().severe("Failed to load reward-editor-gui.yml with BoostedYAML: " + e.getMessage());
        }
    }

    public void openRewardEditor(@NotNull Player player, @NotNull Arena arena) {
        this.playerArenas.put(player.getUniqueId(), arena);
        String title = this.guiConfig.getString("title", "&#FF6B35Reward Editor: &#FFFF00{arena_name}").replace("{arena_name}", arena.getName());
        title = MessageProcessor.process(title);
        int rows = this.guiConfig.getInt("rows", (Integer)6);
        int startSlot = this.guiConfig.getInt("command-area.start-slot", (Integer)0);
        int endSlot = this.guiConfig.getInt("command-area.end-slot", (Integer)(rows * 9 - 10));
        int pageSize = endSlot - startSlot + 1;
        PaginatedGui gui = ((PaginatedBuilder)((PaginatedBuilder)((PaginatedBuilder)Gui.paginated().title(Component.text(title))).rows(rows)).pageSize(pageSize).disableAllInteractions()).create();
        this.setupRewardItems(gui, arena);
        this.setupNavigationItems(gui, arena);
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

    private void setupRewardItems(@NotNull PaginatedGui gui, @NotNull Arena arena) {
        gui.clearPageItems();
        List<String> rewardCommands = this.getArenaRewardCommands(arena);
        for (String command : rewardCommands) {
            GuiItem commandItem = this.createCommandItem(command, arena, gui);
            gui.addItem(commandItem);
        }
    }

    private GuiItem createCommandItem(@NotNull String command, @NotNull Arena arena, @NotNull PaginatedGui gui) {
        GuiItem item;
        String materialName = this.guiConfig.getString("command-item.material", "PAPER");
        String name = this.guiConfig.getString("command-item.name", "&#FFAA00/{command}").replace("{command}", command);
        name = MessageProcessor.process(name);
        List<String> lore = this.guiConfig.getStringList("command-item.lore");
        ArrayList<String> processedLore = new ArrayList<String>();
        for (String line : lore) {
            line = line.replace("{command}", command);
            processedLore.add(MessageProcessor.process(line));
        }
        if (SkullUtils.isBase64Material(materialName)) {
            ItemStack skull = SkullUtils.createSkullFromBase64(materialName);
            item = new GuiItem(skull);
        } else {
            try {
                Material material = Material.valueOf((String)materialName.toUpperCase());
                item = ItemBuilder.from(material).asGuiItem();
            } catch (IllegalArgumentException e) {
                this.plugin.getLogger().warning("Invalid material for command-item in reward-editor-gui.yml: " + materialName);
                item = ItemBuilder.from(Material.PAPER).asGuiItem();
            }
        }
        GuiItem finalItem = item;
        item.setAction(event -> {
            Player player = (Player)event.getWhoClicked();
            Arena playerArena = this.playerArenas.get(player.getUniqueId());
            if (playerArena != null) {
                this.removeRewardCommandFast(player, playerArena, command, gui, finalItem);
            }
        });
        ItemStack itemStack = item.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(name);
            itemMeta.setLore(processedLore);
            itemStack.setItemMeta(itemMeta);
        }
        return item;
    }

    private void setupNavigationItems(@NotNull PaginatedGui gui, @NotNull Arena arena) {
        boolean needsPagination;
        int rows = this.guiConfig.getInt("rows", (Integer)6);
        int lastRowStart = (rows - 1) * 9;
        this.addFillerItems(gui);
        int addCommandSlot = this.guiConfig.getInt("items.add-command.slot", (Integer)49);
        GuiItem addCommandItem = this.createNavigationItem("add-command", event -> {
            Player player = (Player)event.getWhoClicked();
            Arena playerArena = this.playerArenas.get(player.getUniqueId());
            if (playerArena != null) {
                this.startChatInput(player, playerArena);
            }
        });
        gui.setItem(addCommandSlot, addCommandItem);
        List<String> rewardCommands = this.getArenaRewardCommands(arena);
        int startSlot = this.guiConfig.getInt("command-area.start-slot", (Integer)0);
        int endSlot = this.guiConfig.getInt("command-area.end-slot", (Integer)(rows * 9 - 10));
        int pageSize = endSlot - startSlot + 1;
        boolean bl = needsPagination = rewardCommands.size() > pageSize;
        if (needsPagination) {
            int previousSlot = this.guiConfig.getInt("items.previous-page.slot", (Integer)46);
            GuiItem previousItem = this.createNavigationItem("previous-page", event -> gui.previous());
            gui.setItem(previousSlot, previousItem);
            int nextSlot = this.guiConfig.getInt("items.next-page.slot", (Integer)52);
            GuiItem nextItem = this.createNavigationItem("next-page", event -> gui.next());
            gui.setItem(nextSlot, nextItem);
        }
        int backSlot = this.guiConfig.getInt("items.back.slot", (Integer)48);
        GuiItem backItem = this.createNavigationItem("back", event -> {
            Player player = (Player)event.getWhoClicked();
            Arena playerArena = this.playerArenas.get(player.getUniqueId());
            if (playerArena != null) {
                this.plugin.getMainEditorGUI().openMainEditor(player, playerArena);
            }
        });
        gui.setItem(backSlot, backItem);
    }

    private GuiItem createNavigationItem(@NotNull String configPath, @NotNull GuiAction<InventoryClickEvent> handler) {
        GuiItem item;
        String materialName = this.guiConfig.getString("items." + configPath + ".material", "STONE");
        String name = this.guiConfig.getString("items." + configPath + ".name", "Item");
        name = MessageProcessor.process(name);
        List<String> lore = this.guiConfig.getStringList("items." + configPath + ".lore");
        ArrayList<String> processedLore = new ArrayList<String>();
        for (String line : lore) {
            processedLore.add(MessageProcessor.process(line));
        }
        if (SkullUtils.isBase64Material(materialName)) {
            ItemStack skull = SkullUtils.createSkullFromBase64(materialName);
            item = new GuiItem(skull, handler);
        } else {
            try {
                Material material = Material.valueOf((String)materialName.toUpperCase());
                item = ItemBuilder.from(material).asGuiItem(handler);
            } catch (IllegalArgumentException e) {
                this.plugin.getLogger().warning("Invalid material in reward-editor-gui.yml at " + configPath + ": " + materialName);
                item = ItemBuilder.from(Material.STONE).asGuiItem(handler);
            }
        }
        ItemStack itemStack = item.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(name);
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
                this.plugin.getLogger().warning("Invalid filler material in reward-editor-gui.yml: " + materialName);
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
            gui.setItem(slot, filler);
        }
    }

    private void startChatInput(@NotNull Player player, @NotNull Arena arena) {
        player.closeInventory();
        this.chatInputSessions.put(player.getUniqueId(), arena.getName());
        player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.reward.command.prompt")));
        player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.reward.command.placeholder-info")));
        player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.reward.command.stop-info")));
    }

    private void removeRewardCommand(@NotNull Player player, @NotNull Arena arena, @NotNull String command, @NotNull PaginatedGui gui) {
        List<String> rewardCommands = this.getArenaRewardCommands(arena);
        rewardCommands.remove(command);
        this.setArenaRewardCommands(arena, rewardCommands);
        player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.reward.command.removed", "{command}", command, "{arena_name}", arena.getName())));
        this.setupRewardItems(gui, arena);
        this.setupNavigationItems(gui, arena);
    }

    private void removeRewardCommandFast(@NotNull Player player, @NotNull Arena arena, @NotNull String command, @NotNull PaginatedGui gui, @NotNull GuiItem guiItem) {
        List<String> rewardCommands = this.getArenaRewardCommands(arena);
        rewardCommands.remove(command);
        this.setArenaRewardCommands(arena, rewardCommands);
        player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.reward.command.removed", "{command}", command, "{arena_name}", arena.getName())));
        gui.removePageItem(guiItem);
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
        Arena arena = this.plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            this.chatInputSessions.remove(playerId);
            return;
        }
        if ("stop".equalsIgnoreCase(message)) {
            this.chatInputSessions.remove(playerId);
            this.plugin.getUniversalScheduler().runTask((Entity)player, () -> {
                player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.reward.command.cancelled")));
                this.openRewardEditor(player, arena);
            });
            return;
        }
        this.plugin.getUniversalScheduler().runTask((Entity)player, () -> this.handleChatInput(player, arena, message));
    }

    private void handleChatInput(@NotNull Player player, @NotNull Arena arena, @NotNull String command) {
        List<String> rewardCommands = this.getArenaRewardCommands(arena);
        if (rewardCommands == null) {
            rewardCommands = new ArrayList<String>();
        }
        rewardCommands.add(command);
        this.setArenaRewardCommands(arena, rewardCommands);
        this.chatInputSessions.remove(player.getUniqueId());
        player.sendMessage(MessageProcessor.process(this.plugin.getLocaleManager().getMessage("editor.reward.command.added", "{command}", command, "{arena_name}", arena.getName())));
        this.openRewardEditor(player, arena);
    }

    public void reloadGuiConfig() {
        this.loadGuiConfig();
    }

    private List<String> getArenaRewardCommands(@NotNull Arena arena) {
        File arenaFile = new File(this.plugin.getDataFolder(), "arenas/" + arena.getName() + ".yml");
        if (!arenaFile.exists()) {
            return new ArrayList<String>();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration((File)arenaFile);
        List commands = config.getStringList("rewards.commands");
        return commands != null ? new ArrayList<String>(commands) : new ArrayList();
    }

    private void setArenaRewardCommands(@NotNull Arena arena, @NotNull List<String> commands) {
        File arenaFile = new File(this.plugin.getDataFolder(), "arenas/" + arena.getName() + ".yml");
        arenaFile.getParentFile().mkdirs();
        YamlConfiguration config = arenaFile.exists() ? YamlConfiguration.loadConfiguration((File)arenaFile) : new YamlConfiguration();
        config.set("rewards.commands", commands);
        try {
            config.save(arenaFile);
        } catch (Exception e) {
            this.plugin.getLogger().severe("Could not save arena rewards for " + arena.getName() + ": " + e.getMessage());
        }
    }
}

