package me.blurmit.basicsbungee.listener;

import me.blurmit.basicsbungee.BasicsBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class ProxyPingListener implements Listener {

    private final BasicsBungee plugin;
    private final List<String> motdMessages;

    public ProxyPingListener(BasicsBungee plugin) {
        this.plugin = plugin;
        this.motdMessages = plugin.getConfigManager().getConfig().getStringList("Motd");

        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        ServerPing ping = event.getResponse();

        StringBuilder motdBuilder = new StringBuilder();
        StringBuilder motd = new StringBuilder();

        for (int line = 0; line < 2; line++) {
            String motdMessage = motdMessages.get(line);
            motdBuilder.append(ChatColor.translateAlternateColorCodes('&', motdMessage));

            int lineLength = (59 - ChatColor.stripColor(motdMessages.get(0)).length()) / 2;

            if (line == 1) {
                lineLength = lineLength - 2;
            }

            for (int i = 0; i < lineLength; ++i) {
                motdBuilder.insert(0, ' ');
                motdBuilder.append(' ');
            }

            motd.append(motdBuilder);

            if (line == 0) {
                motd.append("\n");
            }

            motdBuilder.delete(0, motdBuilder.length());
        }

        ping.setDescriptionComponent(new TextComponent(motd.toString()));
    }


}
