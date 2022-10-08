package dev.blurmit.basics.listener;

import dev.blurmit.basics.Basics;
import dev.blurmit.basics.util.Placeholders;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class ChatMessageListener implements Listener {

    private final Basics plugin;

    public ChatMessageListener(Basics plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String chatFormat = plugin.getConfigManager().getConfig().getString("Chat-Format");
        String chatMessage = event.getMessage();

        if (event.getPlayer().hasPermission("basics.chat.colors")) {
            event.setMessage(ChatColor.translateAlternateColorCodes('&', chatMessage));
        }

        event.setFormat(Placeholders.parsePlaceholder(chatFormat, event.getPlayer(), event.isAsynchronous()));

    }

}
