package me.blurmit.basicsbungee.listener;

import me.blurmit.basicsbungee.BasicsBungee;
import me.blurmit.basicsbungee.util.Placeholders;
import me.blurmit.basicsbungee.util.PluginMessageHelper;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        if (!event.getPlayer().hasPermission("basics.staffchat")) {
            return;
        }

        // Send staff disconnect message via plugin messaging
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            String player = event.getPlayer().getName();
            String server = event.getPlayer().getServer().getInfo().getName();

            PluginMessageHelper.sendData("RECEIVERS", "", "Staff", "Disconnected", server, player);
        });
    }

    @EventHandler
    public void onServerSwitch(@NotNull ServerSwitchEvent event) {
        plugin.getLimboManager().getLimboPlayers().remove(event.getPlayer().getUniqueId());

        if (!event.getPlayer().hasPermission("basics.staffchat")) {
            return;
        }

        if (event.getFrom() == null) {
            // Send staff connect message via plugin messaging
            plugin.getProxy().getScheduler().runAsync(plugin, () -> {
                String player = event.getPlayer().getDisplayName();
                String server = event.getPlayer().getServer().getInfo().getName();

                PluginMessageHelper.sendData("RECEIVERS", "", "Staff", "Connected", server, player);
            });

            return;
        }

        // Send staff server switch message via plugin messaging
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            String player = event.getPlayer().getDisplayName();
            String originalServer = event.getFrom().getName();
            String newServer = event.getPlayer().getServer().getInfo().getName();

            PluginMessageHelper.sendData("RECEIVERS", "", "Staff", "ServerSwitch", originalServer, player, newServer);
        });
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
