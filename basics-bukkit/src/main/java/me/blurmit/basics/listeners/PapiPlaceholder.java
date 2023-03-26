package me.blurmit.basics.listeners;

import me.blurmit.basics.Basics;
import me.blurmit.basics.events.PlaceholderRequestEvent;
import me.blurmit.basics.util.Placeholders;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class PapiPlaceholder extends PlaceholderExpansion implements Listener {

    private final Basics plugin;

    public PapiPlaceholder(Basics plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        register();
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

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        String placeholder = params.toLowerCase().replace("_", "-");

        // This is a HORRIBLE way to do this, but I'm too tired to care
        try {
            return Placeholders.parse("{" + placeholder + "}");
        } catch (IllegalStateException e) {
            return Placeholders.parse("{" + placeholder + "}", true);
        }
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "basics";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

}
