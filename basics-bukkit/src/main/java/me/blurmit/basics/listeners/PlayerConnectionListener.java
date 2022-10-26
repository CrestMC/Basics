package me.blurmit.basics.listeners;

import me.blurmit.basics.Basics;
import me.blurmit.basics.util.placeholder.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private final Basics plugin;

    public PlayerConnectionListener(Basics plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(Placeholders.parsePlaceholder(plugin.getConfigManager().getConfig().getString("Join-Message"), player));
        plugin.getConfigManager().getConfig().getStringList("MOTD").forEach(line -> player.sendMessage(Placeholders.parsePlaceholder(line, event.getPlayer())));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(Placeholders.parsePlaceholder(plugin.getConfigManager().getConfig().getString("Quit-Message"), event.getPlayer()));
    }

}
