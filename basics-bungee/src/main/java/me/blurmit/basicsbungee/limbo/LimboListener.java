package me.blurmit.basicsbungee.limbo;

import me.blurmit.basicsbungee.BasicsBungee;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LimboListener implements Listener {

    private final BasicsBungee plugin;

    public LimboListener(BasicsBungee plugin) {
        this.plugin = plugin;

        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        plugin.getLimboManager().getKeepAliveTasks().remove(event.getPlayer().getUniqueId());
    }

}
