/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.chat.ClickEvent
 *  net.md_5.bungee.api.chat.ClickEvent$Action
 *  net.md_5.bungee.api.chat.ComponentBuilder
 *  net.md_5.bungee.api.chat.TextComponent
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package hu.geri.utils;

import hu.geri.CocoFFA;
import hu.geri.libs.universalScheduler.UniversalScheduler;
import hu.geri.libs.universalScheduler.scheduling.schedulers.TaskScheduler;
import hu.geri.processor.MessageProcessor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class UpdateChecker {
    private static final String DOWNLOAD_URL = "https://www.spigotmc.org/resources/cocoffa.128313/";
    private static final Pattern LINK_TAG = Pattern.compile("\\[link](.*?)\\[/link]", 2);
    private final CocoFFA plugin;
    private final int resourceId;
    private final TaskScheduler scheduler;
    private volatile String latestVersion = null;
    private volatile boolean updateAvailable = false;

    public UpdateChecker(CocoFFA plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
        this.scheduler = UniversalScheduler.getScheduler((Plugin)plugin);
    }

    public void init() {
        this.scheduler.runTaskAsynchronously(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection)new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                this.latestVersion = reader.readLine().trim();
                reader.close();
                String currentVersion = this.plugin.getDescription().getVersion();
                if (!currentVersion.equalsIgnoreCase(this.latestVersion)) {
                    this.updateAvailable = true;
                    this.scheduler.runTask(() -> {
                        String consoleMessage = this.plugin.getLocaleManager().getMessage("update.console", "%latest%", this.latestVersion, "%current%", currentVersion);
                        this.plugin.getLogger().warning(ChatColor.stripColor((String)consoleMessage));
                    });
                }
            } catch (IOException e) {
                String errorMessage = this.plugin.getLocaleManager().getMessage("update.error", "%error%", e.getMessage());
                this.plugin.getLogger().warning(errorMessage);
            }
        });
    }

    public void notifyIfUpdateAvailable(Player player) {
        if (!this.updateAvailable || !player.hasPermission("cocoffa.admin.update-notify")) {
            return;
        }
        this.scheduler.runTask(() -> {
            if (player.isOnline()) {
                String raw = this.plugin.getLocaleManager().getMessage("update.player", "%latest%", this.latestVersion);
                this.sendWithClickableLink(player, raw, DOWNLOAD_URL);
            }
        });
    }

    private void sendWithClickableLink(Player player, String rawMessage, String url) {
        String colored = MessageProcessor.process(rawMessage);
        Matcher m = LINK_TAG.matcher(colored);
        if (!m.find()) {
            this.plugin.getLocaleManager().sendMessage(player, "update.player", "%latest%", this.latestVersion);
            return;
        }
        String before = colored.substring(0, m.start());
        String linkText = m.group(1);
        String after = colored.substring(m.end());
        ComponentBuilder builder = new ComponentBuilder();
        if (!before.isEmpty()) {
            BaseComponent[] beforeComp = TextComponent.fromLegacyText((String)before);
            builder.append(beforeComp);
        }
        TextComponent linkComp = new TextComponent(TextComponent.fromLegacyText((String)linkText));
        linkComp.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        builder.append((BaseComponent)linkComp);
        if (!after.isEmpty()) {
            BaseComponent[] afterComp = TextComponent.fromLegacyText((String)after);
            builder.append(afterComp);
        }
        player.spigot().sendMessage(builder.create());
    }
}

