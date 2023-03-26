package me.blurmit.basics.listeners;

import me.blurmit.basics.Basics;
import me.blurmit.basics.util.Placeholders;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class AsyncChatListener implements Listener {

    private final Basics plugin;

    public AsyncChatListener(Basics plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        String format = plugin.getConfigManager().getConfig().getString("Chat-Format");

        if (event.getPlayer().hasPermission("basics.chat.colors")) {
            event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
        }

        event.setFormat(Placeholders.parse(format, event.getPlayer(), event.isAsynchronous()));

    }

}
