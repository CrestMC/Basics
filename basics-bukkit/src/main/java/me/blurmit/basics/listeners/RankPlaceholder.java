package me.blurmit.basics.listeners;

import me.blurmit.basics.Basics;
import me.blurmit.basics.events.PlaceholderRequestEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RankPlaceholder implements Listener {

    private final Basics plugin;

    public RankPlaceholder(Basics plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlaceholderRequest(PlaceholderRequestEvent event) {
        String placeholder = event.getPlaceholder().toLowerCase();

        if (placeholder.equalsIgnoreCase("player-rank-displayname")) {
            try {
                event.setResponse(plugin.getRankManager().getHighestRankByPriority(event.getPlayer()).getDisplayName());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equalsIgnoreCase("player-rank-name")) {
            try {
                event.setResponse(plugin.getRankManager().getHighestRankByPriority(event.getPlayer()).getName());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equalsIgnoreCase("player-rank-prefix")) {
            try {
                event.setResponse(plugin.getRankManager().getHighestRankByPriority(event.getPlayer()).getPrefix());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equalsIgnoreCase("player-rank-suffix")) {
            try {
                event.setResponse(plugin.getRankManager().getHighestRankByPriority(event.getPlayer()).getSuffix());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equalsIgnoreCase("player-rank-color")) {
            try {
                event.setResponse(plugin.getRankManager().getHighestRankByPriority(event.getPlayer()).getColor());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equalsIgnoreCase("player-rank-priority")) {
            try {
                event.setResponse(plugin.getRankManager().getHighestRankByPriority(event.getPlayer()).getPriority() + "");
            } catch (Exception e) {
                event.setResponse("");
            }
        }
    }

}
