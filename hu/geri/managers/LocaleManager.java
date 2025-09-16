/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package hu.geri.managers;

import hu.geri.CocoFFA;
import hu.geri.libs.boostedyaml.YamlDocument;
import hu.geri.libs.boostedyaml.dvs.versioning.BasicVersioning;
import hu.geri.libs.boostedyaml.settings.dumper.DumperSettings;
import hu.geri.libs.boostedyaml.settings.general.GeneralSettings;
import hu.geri.libs.boostedyaml.settings.loader.LoaderSettings;
import hu.geri.libs.boostedyaml.settings.updater.UpdaterSettings;
import hu.geri.processor.MessageProcessor;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocaleManager {
    private final CocoFFA plugin;
    private YamlDocument localeConfig;

    public LocaleManager(CocoFFA plugin) {
        this.plugin = plugin;
    }

    public void loadLocale() {
        File localeDir = new File(this.plugin.getDataFolder(), "locale");
        if (!localeDir.exists()) {
            localeDir.mkdirs();
        }
        this.copyLocaleFileIfNotExists("messages_en.yml");
        this.copyLocaleFileIfNotExists("messages_hu.yml");
        String localeFileName = this.plugin.getConfigManager().getLocaleFile();
        File localeFile = new File(String.valueOf(this.plugin.getDataFolder()) + "/locale", localeFileName);
        try {
            this.localeConfig = YamlDocument.create(localeFile, this.plugin.getResource("locale/" + localeFileName), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("locale-version")).setKeepAll(true).build());
        } catch (IOException e) {
            this.plugin.getLogger().severe("Failed to load locale file with BoostedYAML: " + e.getMessage());
        }
    }

    private void copyLocaleFileIfNotExists(String fileName) {
        File localeFile = new File(String.valueOf(this.plugin.getDataFolder()) + "/locale", fileName);
        if (!localeFile.exists()) {
            this.plugin.saveResource("locale/" + fileName, false);
        }
    }

    public String getMessage(String path) {
        return this.getMessage(path, new String[0]);
    }

    public String getMessage(String path, String ... placeholders) {
        String message = this.localeConfig.getString(path, "&cMessage not found: " + path);
        if (message == null || message.trim().isEmpty()) {
            return null;
        }
        String prefix = this.plugin.getConfigManager().getPrefix();
        message = message.replace("%prefix%", prefix);
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 >= placeholders.length) continue;
            message = message.replace(placeholders[i], placeholders[i + 1]);
        }
        return this.colorize(message);
    }

    public List<String> getMessageList(String path) {
        return this.localeConfig.getStringList(path);
    }

    public String colorize(String message) {
        return MessageProcessor.process(message);
    }

    public void sendMessage(Player player, String path, String ... placeholders) {
        String message = this.getMessage(path, placeholders);
        if (message != null && !message.trim().isEmpty()) {
            player.sendMessage(message);
        }
    }

    public void sendMessage(CommandSender sender, String path, String ... placeholders) {
        String message = this.getMessage(path, placeholders);
        if (message != null && !message.trim().isEmpty()) {
            sender.sendMessage(message);
        }
    }
}

