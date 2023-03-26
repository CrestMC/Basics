package me.blurmit.basicsbungee.listener;

import me.blurmit.basicsbungee.BasicsBungee;
import me.blurmit.basicsbungee.util.Placeholders;
import me.blurmit.basicsbungee.util.PluginMessageHelper;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayerConnectionListener implements Listener {

    private final List<String> tablistHeader;
    private final List<String> tablistFooter;
    private final BasicsBungee plugin;

    public PlayerConnectionListener(BasicsBungee plugin) {
        this.tablistHeader = plugin.getConfigManager().getConfig().getStringList("Tablist.Header");
        this.tablistFooter = plugin.getConfigManager().getConfig().getStringList("Tablist.Footer");
        this.plugin = plugin;

        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        setupTabListHeader(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDisconnect(@NotNull PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (!player.hasPermission("basics.staffchat")) {
            return;
        }

        // Send staff disconnect message via plugin messaging
        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            String playerName = player.getName();
            ServerInfo server = player.getServer().getInfo();
            String serverName;

            if (server != null) {
                serverName = player.getServer().getInfo().getName();;
            } else {
                serverName = "Unknown";
                playerName = "invalid_" + playerName;
            }

            PluginMessageHelper.sendData("receivers", "", "Staff", "Disconnected", serverName, playerName);
        }, 25, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onServerSwitch(@NotNull ServerSwitchEvent event) {
        plugin.getLimboManager().getLimboPlayers().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Server server = player.getServer();
        String serverName = event.getServer().getInfo().getName();

        if (!player.hasPermission("basics.staffchat")) {
            return;
        }

        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            if (server == null) {
                // Send staff connect message via plugin messaging
                String playerName = player.getDisplayName();

                PluginMessageHelper.sendData("receivers", "", "Staff", "Connected", serverName, playerName);
                return;
            }

            // Send staff server switch message via plugin messaging
            String playerName = player.getDisplayName();
            String originalServer = server.getInfo().getName();

            PluginMessageHelper.sendData("receivers", "", "Staff", "ServerSwitch", originalServer, playerName, serverName);
        }, 25, TimeUnit.MILLISECONDS);
    }

    private void setupTabListHeader(ProxiedPlayer player) {
        ComponentBuilder headerBuilder = new ComponentBuilder();
        ComponentBuilder footerBuilder = new ComponentBuilder();

        for (int i = 0; i < tablistHeader.size(); i++) {
            headerBuilder.append(Placeholders.parsePlaceholder(tablistHeader.get(i), player));

            if (i != tablistHeader.size() - 1) {
                headerBuilder.append("\n");
            }
        }

        for (int i = 0; i < tablistFooter.size(); i++) {
            footerBuilder.append(Placeholders.parsePlaceholder(tablistFooter.get(i), player));

            if (i != tablistFooter.size() - 1) {
                footerBuilder.append("\n");
            }
        }

        player.setTabHeader(headerBuilder.create(), footerBuilder.create());
    }
}
