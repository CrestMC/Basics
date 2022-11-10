package me.blurmit.basics.placeholder;

import me.blurmit.basics.Basics;
import me.blurmit.basics.events.PlaceholderRequestEvent;
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
        String placeholder = event.getPlaceholder().toLowerCase();

        if (placeholder.startsWith("papi-")) {
            try {
                event.setResponse("Papi Test");
            } catch (Exception e) {
                event.setResponse("");
            }
        }
    }

}
