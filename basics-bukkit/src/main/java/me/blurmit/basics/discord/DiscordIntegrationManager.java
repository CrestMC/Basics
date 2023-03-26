package me.blurmit.basics.discord;

import me.blurmit.basics.Basics;
import me.blurmit.basics.util.DiscordColors;
import me.blurmit.basics.util.HttpRequestUtil;
import me.blurmit.basics.util.Placeholders;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DiscordIntegrationManager {

    private final Basics plugin;
    private final FileConfiguration config;

    private String webhookURL;
    private String webhookProfilePicture;
    private String webhookName;

    private String chatFormat;
    private boolean colorCodesEnabled;

    public DiscordIntegrationManager(Basics plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getConfig();

        if (!config.getBoolean("Discord-Integration.Enabled")) {
            return;
        }

        plugin.getServer().getPluginManager().registerEvents(new DiscordIntegrationListener(plugin), plugin);

        load();
    }

    public void load() {
        this.webhookProfilePicture = config.getString("Discord-Integration.Webhook-Profile-Picture");
        this.webhookName = config.getString("Discord-Integration.Webhook-Name");
        this.webhookURL = config.getString("Discord-Integration.Webhook-URL");

        this.chatFormat = config.getString("Discord-Integration.Chat-Format");
        this.colorCodesEnabled = config.getBoolean("Discord-Integration.Color-Codes-Enabled");
    }

    public void sendChatMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        String chatFormat = this.chatFormat;

        if (player.hasPermission("basics.chat.colors")) {
            message = ChatColor.translateAlternateColorCodes('&', message);
        }

        if (colorCodesEnabled) {
            message = message.replace("`", "‌`");

            chatFormat = chatFormat.replace("{message}", message);
            chatFormat = Placeholders.parse(chatFormat, player, event.isAsynchronous());
            chatFormat = "```ansi\n" + DiscordColors.translate('§', chatFormat) + "\n```";
        } else {
            chatFormat = chatFormat.replace("{message}", message);
            chatFormat = Placeholders.parse(chatFormat, player, event.isAsynchronous());
        }

        sendContent(chatFormat);
    }

    public void sendContent(String content) {
        Map<String, Object> data =  new HashMap<>();
        data.put("content", content);
        data.put("username", webhookName);
        data.put("avatar_url", webhookProfilePicture);
        data.put("allowed_mentions", new String[0]);

        HttpRequestUtil.dispatchDiscordWebhook(webhookURL, data);
    }

}
