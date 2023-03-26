package me.blurmit.basics.discord;

import me.blurmit.basics.Basics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class DiscordIntegrationListener implements Listener {

    private final Basics plugin;

    public DiscordIntegrationListener(Basics plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        plugin.getDiscordIntegrationManager().sendChatMessage(event);
    }

}
