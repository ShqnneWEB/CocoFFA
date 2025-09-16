/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  club.minnced.discord.webhook.WebhookClient
 *  club.minnced.discord.webhook.send.WebhookEmbed
 *  club.minnced.discord.webhook.send.WebhookEmbed$EmbedFooter
 *  club.minnced.discord.webhook.send.WebhookEmbed$EmbedTitle
 *  club.minnced.discord.webhook.send.WebhookEmbedBuilder
 */
package hu.geri.managers;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import hu.geri.CocoFFA;
import hu.geri.config.Config;
import hu.geri.libs.boostedyaml.block.implementation.Section;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class WebhookManager {
    private final CocoFFA plugin;
    private Config webhookConfig;
    private final Map<String, WebhookClient> webhookClients = new HashMap<String, WebhookClient>();

    public WebhookManager(CocoFFA plugin) {
        this.plugin = plugin;
    }

    public void loadWebhookConfig() {
        File webhookFile = new File(this.plugin.getDataFolder(), "webhook.yml");
        this.webhookConfig = new Config(webhookFile);
        this.initializeWebhookClients();
    }

    private void initializeWebhookClients() {
        this.closeAllClients();
        for (String key : this.webhookConfig.getKeys(false)) {
            String url = this.webhookConfig.getString(key + ".url");
            if (url == null || url.equals("https://discord.com/api/webhooks/URL")) continue;
            try {
                WebhookClient client = WebhookClient.withUrl((String)url);
                this.webhookClients.put(key, client);
            } catch (Exception e) {
                this.plugin.getLogger().warning("Invalid webhook URL for " + key + ": " + e.getMessage());
            }
        }
    }

    public void sendWebhook(String eventType, Map<String, String> placeholders) {
        WebhookClient client = this.webhookClients.get(eventType);
        if (client == null) {
            return;
        }
        Section embedSection = this.webhookConfig.getSection(eventType + ".embed");
        if (embedSection == null) {
            return;
        }
        String title = embedSection.getString("title", "");
        String description = embedSection.getString("description", "");
        String colorHex = embedSection.getString("color", "#FFFFFF");
        String footer = embedSection.getString("footer", "");
        String image = embedSection.getString("image", "");
        String thumbnail = embedSection.getString("thumbnail", "");
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                String placeholder = "%" + entry.getKey() + "%";
                String value = entry.getValue();
                title = title.replace(placeholder, value);
                description = description.replace(placeholder, value);
                footer = footer.replace(placeholder, value);
            }
        }
        WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder().setTitle(new WebhookEmbed.EmbedTitle(title, null)).setDescription(description).setColor(Integer.valueOf(this.parseColor(colorHex)));
        if (!footer.isEmpty()) {
            embedBuilder.setFooter(new WebhookEmbed.EmbedFooter(footer, null));
        }
        if (!image.isEmpty()) {
            embedBuilder.setImageUrl(image);
        }
        if (!thumbnail.isEmpty()) {
            embedBuilder.setThumbnailUrl(thumbnail);
        }
        WebhookEmbed embed = embedBuilder.build();
        CompletionStage future = ((CompletableFuture)client.send(embed, new WebhookEmbed[0]).thenAccept(message -> this.plugin.getLogger().info("Successfully sent webhook for event: " + eventType))).exceptionally(throwable -> {
            this.plugin.getLogger().warning("Failed to send webhook for event " + eventType + ": " + throwable.getMessage());
            return null;
        });
    }

    private int parseColor(String colorHex) {
        try {
            if (colorHex.startsWith("#")) {
                colorHex = colorHex.substring(1);
            }
            return Integer.parseInt(colorHex, 16);
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }

    public void reloadWebhookConfig() {
        this.loadWebhookConfig();
    }

    public void closeAllClients() {
        for (WebhookClient client : this.webhookClients.values()) {
            try {
                client.close();
            } catch (Exception e) {
                this.plugin.getLogger().warning("Error closing webhook client: " + e.getMessage());
            }
        }
        this.webhookClients.clear();
    }

    public void sendWinWebhook(String player, String arena, int kills) {
        HashMap<String, String> placeholders = new HashMap<String, String>();
        placeholders.put("player", player);
        placeholders.put("arena", arena);
        placeholders.put("kills", String.valueOf(kills));
        this.sendWebhook("win", placeholders);
    }

    public void sendJoinWebhook(String player, String arena, int currentPlayers, int maxPlayers) {
        HashMap<String, String> placeholders = new HashMap<String, String>();
        placeholders.put("player", player);
        placeholders.put("arena", arena);
        placeholders.put("current_players", String.valueOf(currentPlayers));
        placeholders.put("max_players", String.valueOf(maxPlayers));
        this.sendWebhook("join", placeholders);
    }

    public void sendLeaveWebhook(String player, String arena, int currentPlayers, int maxPlayers) {
        HashMap<String, String> placeholders = new HashMap<String, String>();
        placeholders.put("player", player);
        placeholders.put("arena", arena);
        placeholders.put("current_players", String.valueOf(currentPlayers));
        placeholders.put("max_players", String.valueOf(maxPlayers));
        this.sendWebhook("leave", placeholders);
    }

    public void sendStartWebhook(String arena, int playerCount) {
        HashMap<String, String> placeholders = new HashMap<String, String>();
        placeholders.put("arena", arena);
        placeholders.put("player_count", String.valueOf(playerCount));
        this.sendWebhook("start", placeholders);
    }

    public void sendStopWebhook(String arena, String reason) {
        HashMap<String, String> placeholders = new HashMap<String, String>();
        placeholders.put("arena", arena);
        placeholders.put("reason", reason);
        this.sendWebhook("stop", placeholders);
    }

    public void sendKillWebhook(String victim, String killer, String arena) {
        HashMap<String, String> placeholders = new HashMap<String, String>();
        placeholders.put("victim", victim);
        placeholders.put("killer", killer);
        placeholders.put("arena", arena);
        this.sendWebhook("kill", placeholders);
    }
}

