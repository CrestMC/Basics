package me.blurmit.basicsbungee.limbo;

import me.blurmit.basicsbungee.BasicsBungee;
import me.blurmit.basicsbungee.limbo.server.LimboServer;
import me.blurmit.basicsbungee.util.Messages;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
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
        plugin.getLimboManager().getLimboPlayers().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        event.setCancelled(true);
        event.setCancelServer(new LimboServer());
        event.setKickReasonComponent(event.getKickReasonComponent());

        event.getPlayer().sendMessage(Messages.SERVER_KICK.text());
        event.getPlayer().sendMessage(event.getKickReasonComponent());

        plugin.getLimboManager().banishToLimbo(event.getPlayer());
    }

}
