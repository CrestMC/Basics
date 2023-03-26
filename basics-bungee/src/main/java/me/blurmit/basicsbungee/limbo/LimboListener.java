package me.blurmit.basicsbungee.limbo;

import me.blurmit.basicsbungee.BasicsBungee;
import me.blurmit.basicsbungee.limbo.server.LimboServer;
import me.blurmit.basicsbungee.util.Messages;
import me.blurmit.basicsbungee.util.Placeholders;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

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
        ProxiedPlayer player = event.getPlayer();
        String serverName = player.getServer().getInfo().getName();
        ServerInfo server = plugin.getProxy().getServers().values().stream()
                .filter(serverInfo -> !serverInfo.getName().equals(serverName))
                .filter(serverInfo -> !serverInfo.isRestricted())
                .findFirst()
                .orElse(new LimboServer());

        if (server instanceof LimboServer) {
            plugin.getLimboManager().banishToLimbo(player);
        }

        event.setCancelServer(server);
        event.setCancelled(true);

        player.sendMessage(Placeholders.parsePlaceholder(Messages.SERVER_KICK + "", player, false, event.getKickReason()));
    }

}
