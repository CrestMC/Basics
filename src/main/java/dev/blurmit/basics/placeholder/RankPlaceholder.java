package dev.blurmit.basics.placeholder;

import dev.blurmit.basics.Basics;
import dev.blurmit.basics.event.PlaceholderRequestEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RankPlaceholder implements Listener {

    public RankPlaceholder(Basics plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlaceholderRequest(PlaceholderRequestEvent event) {
        String placeholder = event.getPlaceholder().toLowerCase();

        if (placeholder.startsWith("player-rank")) {
            try {
                if (event.getPlayer().isOp()) {
                    event.setResponse(ChatColor.RED + "" + ChatColor.BOLD + "MANAGER");
                } else {
                    event.setResponse(ChatColor.GRAY + "GUEST");
                }
            } catch (Exception e) {
                event.setResponse("");
            }
        }
    }

}
