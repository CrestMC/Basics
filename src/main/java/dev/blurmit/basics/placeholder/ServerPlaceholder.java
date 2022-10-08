package dev.blurmit.basics.placeholder;

import dev.blurmit.basics.Basics;
import dev.blurmit.basics.event.PlaceholderRequestEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerPlaceholder implements Listener {

    public ServerPlaceholder(Basics plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlaceholderRequest(PlaceholderRequestEvent event) {
        String placeholder = event.getPlaceholder().toLowerCase();

        if (placeholder.startsWith("player-count-")) {
            try {
                event.setResponse(Bukkit.getOnlinePlayers().size() + "");
            } catch (Exception e) {
                event.setResponse("");
            }
        }
    }

}
