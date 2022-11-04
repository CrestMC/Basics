package me.blurmit.basicsbungee.listener;

import me.blurmit.basicsbungee.BasicsBungee;
import me.blurmit.basicsbungee.util.pluginmessage.PluginMessageHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class PluginMessageListener implements Listener {

    private final BasicsBungee plugin;

    public PluginMessageListener(BasicsBungee plugin) {
        this.plugin = plugin;

        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals("BungeeCord")) {
            return;
        }

        try {
            DataInput input = new DataInputStream(new ByteArrayInputStream(event.getData()));
            String channel = input.readUTF();
            ServerInfo serverData = plugin.getProxy().getServers().values().stream().filter(serverInfo -> serverInfo.getSocketAddress().equals(event.getSender().getSocketAddress())).findFirst().orElse(null);

            switch (channel) {
                case "ServerStatus": {
                    Set<String> serverStatus = new HashSet<>();

                    for (ServerInfo server : plugin.getProxy().getServers().values()) {
                        server.ping((result, error) -> {
                            serverStatus.add(error == null ? server.getName() + ":" + result.getPlayers().getOnline() + "/" + result.getPlayers().getMax() : server.getName() + ":" + ChatColor.RED + "Offline");

                            if (serverStatus.size() == plugin.getProxy().getServers().values().size()) {
                                PluginMessageHelper.sendData(serverData.getName(), serverData.getName(), channel, String.join(", ", serverStatus));
                            }
                        });
                    }
                    break;
                }
                case "Staff-Chat": {
                    String server = input.readUTF();
                    String player = input.readUTF();
                    String message = ChatColor.translateAlternateColorCodes('&', input.readUTF());
                    PluginMessageHelper.sendData("RECEIVERS", serverData.getName(), "Staff-Chat", server, player, message);
                    break;
                }
                case "HelpOP-Request": {
                    String player = input.readUTF();
                    String message = input.readUTF();
                    PluginMessageHelper.sendData("RECEIVERS", serverData.getName(), "HelpOP-Request", player, message);
                }
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst attempting to read plugin message", e);
        }
    }

}
