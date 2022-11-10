package me.blurmit.basicsbungee.listener;

import me.blurmit.basicsbungee.BasicsBungee;
import me.blurmit.basicsbungee.util.Placeholders;
import me.blurmit.basicsbungee.util.pluginmessage.PluginMessageHelper;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
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
        // Set tablist header based off of the provided configuration
        ComponentBuilder headerBuilder = new ComponentBuilder();
        ComponentBuilder footerBuilder = new ComponentBuilder();

        for (int i = 0; i < tablistHeader.size(); i++) {
            headerBuilder.append(Placeholders.parsePlaceholder(tablistHeader.get(i), event.getPlayer()));

            if (i != tablistHeader.size() - 1) {
                headerBuilder.append("\n");
            }
        }

        for (int i = 0; i < tablistFooter.size(); i++) {
            footerBuilder.append(Placeholders.parsePlaceholder(tablistFooter.get(i), event.getPlayer()));

            if (i != tablistFooter.size() - 1) {
                footerBuilder.append("\n");
            }
        }

        event.getPlayer().setTabHeader(headerBuilder.create(), footerBuilder.create());
    }

    @EventHandler
    public void onServerDisconnect(@NotNull ServerDisconnectEvent event) {
        if (!event.getPlayer().hasPermission("basics.staffchat")) {
            return;
        }

        if (event.getPlayer().getServer().getInfo().getName().equals(event.getTarget().getName())) {
            // Send staff disconnect message via plugin messaging
            String player = event.getPlayer().getName();
            String server = event.getTarget().getName();

            plugin.getProxy().getScheduler().schedule(plugin, () -> {
                PluginMessageHelper.sendData("RECEIVERS", "", "Staff-Disconnected", server, player);
            }, 250, TimeUnit.MILLISECONDS);
        } else {
            // Send staff server switch message via plugin messaging
            String player = event.getPlayer().getDisplayName();
            String originalServer = event.getTarget().getName();
            String newServer = event.getPlayer().getServer().getInfo().getName();


            plugin.getProxy().getScheduler().schedule(plugin, () -> {
                PluginMessageHelper.sendData("RECEIVERS", "", "Staff-ServerSwitch", player, originalServer, newServer);
            }, 250, TimeUnit.MILLISECONDS);
        }
    }

    @EventHandler
    public void onServerSwitch(@NotNull ServerSwitchEvent event) {
        if (event.getFrom() != null) {
            return;
        }

        if (!event.getPlayer().hasPermission("basics.staffchat")) {
            return;
        }

        // Send staff connect message via plugin messaging
        String player = event.getPlayer().getDisplayName();
        String server = event.getPlayer().getServer().getInfo().getName();

        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            PluginMessageHelper.sendData("RECEIVERS", "", "Staff-Connected", server, player);
        }, 250, TimeUnit.MILLISECONDS);
    }

}
