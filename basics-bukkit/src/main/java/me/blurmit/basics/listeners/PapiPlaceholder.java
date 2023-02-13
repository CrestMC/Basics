package me.blurmit.basics.listeners;

import me.blurmit.basics.Basics;
import me.blurmit.basics.events.PlaceholderRequestEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PapiPlaceholder implements Listener {

    private final Basics plugin;

    public PapiPlaceholder(Basics plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlaceholderRequest(PlaceholderRequestEvent event) {
        String placeholder = event.getPlaceholder();
        String modifiedPlaceholder = "%" + placeholder.replaceFirst("papi-", "") + "%";

        if (placeholder.startsWith("papi-")) {
            try {
                event.setResponse(PlaceholderAPI.setPlaceholders(event.getPlayer(), modifiedPlaceholder));
            } catch (Exception e) {
                event.setResponse("");
            }
        }
    }

}
