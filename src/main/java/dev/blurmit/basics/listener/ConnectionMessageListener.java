package dev.blurmit.basics.listener;

import dev.blurmit.basics.Basics;
import dev.blurmit.basics.util.Placeholders;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionMessageListener implements Listener {

    private final Basics plugin;

    public ConnectionMessageListener(Basics plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(Placeholders.parsePlaceholder(plugin.getConfigManager().getConfig().getString("Join-Message"), event.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(Placeholders.parsePlaceholder(plugin.getConfigManager().getConfig().getString("Quit-Message"), event.getPlayer()));
    }

}
