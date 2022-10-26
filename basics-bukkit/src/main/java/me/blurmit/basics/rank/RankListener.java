package me.blurmit.basics.rank;

import me.blurmit.basics.Basics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class RankListener implements Listener {

    private final Basics plugin;

    public RankListener(Basics plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getRankManager().loadRankData(event.getPlayer().getUniqueId());
        plugin.getRankManager().getActiveAttachments().put(event.getPlayer().getUniqueId(), plugin.getRankManager().loadPermissions(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        plugin.getRankManager().getStorage().getOwnedRanks().remove(event.getPlayer().getUniqueId());
        plugin.getRankManager().getActiveAttachments().remove(event.getPlayer().getUniqueId());
    }

}
