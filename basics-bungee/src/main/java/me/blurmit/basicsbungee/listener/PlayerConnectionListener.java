package me.blurmit.basicsbungee.listener;

import com.mojang.brigadier.Message;
import me.blurmit.basicsbungee.BasicsBungee;
import me.blurmit.basicsbungee.limbo.LimboServer;
import me.blurmit.basicsbungee.util.Placeholders;
import me.blurmit.basicsbungee.util.lang.Messages;
import me.blurmit.basicsbungee.util.pluginmessage.PluginMessageHelper;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
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
        plugin.getLimboManager().getKeepAliveTasks().remove(event.getPlayer().getUniqueId());

        // Send staff disconnect message via plugin messaging
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            String player = event.getPlayer().getName();
            String server = event.getPlayer().getServer().getInfo().getName();

            PluginMessageHelper.sendData("RECEIVERS", "", "Staff-Disconnected", server, player);
        });
    }

    @EventHandler
    public void onServerSwitch(@NotNull ServerSwitchEvent event) {
        plugin.getLimboManager().getKeepAliveTasks().remove(event.getPlayer().getUniqueId());

        if (event.getFrom() == null) {
            // Send staff connect message via plugin messaging
            plugin.getProxy().getScheduler().runAsync(plugin, () -> {
                String player = event.getPlayer().getDisplayName();
                String server = event.getPlayer().getServer().getInfo().getName();

                PluginMessageHelper.sendData("RECEIVERS", "", "Staff-Connected", server, player);
            });

            return;
        }

        if (!event.getPlayer().hasPermission("basics.staffchat")) {
            return;
        }

        // Send staff server switch message via plugin messaging
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            String player = event.getPlayer().getDisplayName();
            String originalServer = event.getFrom().getName();
            String newServer = event.getPlayer().getServer().getInfo().getName();

            PluginMessageHelper.sendData("RECEIVERS", "", "Staff-ServerSwitch", player, originalServer, newServer);
        });
    }

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
//        event.setCancelled(true);
//        event.setKickReasonComponent(event.getKickReasonComponent());
//
//        event.getPlayer().sendMessage(Messages.SERVER_KICK.text());
//        event.getPlayer().sendMessage(event.getKickReasonComponent());
//
//        plugin.getLimboManager().banishToLimbo(event.getPlayer());
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
