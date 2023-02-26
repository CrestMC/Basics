package me.blurmit.basicsbungee.listener;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import me.blurmit.basicsbungee.BasicsBungee;
import me.blurmit.basicsbungee.util.Placeholders;
import me.blurmit.basicsbungee.util.PluginMessageHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class PluginMessageListener implements Listener {

    private final BasicsBungee plugin;
    private final Set<String> whitelistedServers;

    public PluginMessageListener(BasicsBungee plugin) {
        this.plugin = plugin;
        this.whitelistedServers = new HashSet<>();

        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getTag().equals("minecraft:brand") || event.getTag().equals("MC|Brand")) {
            PluginMessage pluginMessage = new PluginMessage();
            pluginMessage.setTag(event.getTag());

            ByteBuf brand = Unpooled.wrappedBuffer(event.getData());
            brand.release();

            brand = ByteBufAllocator.DEFAULT.heapBuffer();
            DefinedPacket.writeString(plugin.getConfigManager().getConfig().getString("Server-Brand"), brand);
            pluginMessage.setData(DefinedPacket.toArray(brand));
            brand.release();

            event.getSender().unsafe().sendPacket(pluginMessage);
            event.setCancelled(true);
            return;
        }

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

                    for (int serverID = 0; serverID < plugin.getProxy().getServers().values().size(); serverID++) {
                        ServerInfo server = new ArrayList<>(plugin.getProxy().getServers().values()).get(serverID);
                        String senderName = serverData.getName();
                        int finalServerID = serverID;

                        server.ping((result, error) -> {
                            if (whitelistedServers.contains(server.getName())) {
                                String whitelistMessage = Placeholders.parsePlaceholder(plugin.getConfigManager().getConfig().getString("Server-Status.Whitelist"));
                                serverStatus.add(server.getName() + ":" + whitelistMessage);
                            } else if (error == null) {
                                ServerPing.Players players = result.getPlayers();
                                String onlineMessage = Placeholders.parsePlaceholder(plugin.getConfigManager().getConfig().getString("Server-Status.Online"), players.getOnline() + "", players.getMax() + "");
                                serverStatus.add(server.getName() + ":" + onlineMessage);
                            } else {
                                String offlineMessage = Placeholders.parsePlaceholder(plugin.getConfigManager().getConfig().getString("Server-Status.Offline"));
                                serverStatus.add(server.getName() + ":" + offlineMessage);
                            }

                            if (finalServerID == (plugin.getProxy().getServers().values().size() - 1)) {
                                PluginMessageHelper.sendData(senderName, senderName, channel, String.join(", ", serverStatus));
                            }
                        });
                    }
                    break;
                }
                case "WhitelistStatus": {
                    String serverName = serverData.getName();
                    boolean isWhitelisted = Boolean.parseBoolean(input.readUTF());

                    if (!isWhitelisted) {
                        whitelistedServers.remove(serverName);
                        return;
                    }

                    if (whitelistedServers.contains(serverName)) {
                        return;
                    }

                    whitelistedServers.add(serverName);
                    break;
                }
                case "Staff": {
                    if (!input.readUTF().equals("Chat")) {
                        return;
                    }

                    String server = input.readUTF();
                    String player = input.readUTF();
                    String message = ChatColor.translateAlternateColorCodes('&', input.readUTF());
                    PluginMessageHelper.sendData("RECEIVERS", serverData.getName(), "Staff", "Chat", server, player, message);
                    break;
                }
                case "HelpOP-Request": {
                    String player = input.readUTF();
                    String message = input.readUTF();
                    PluginMessageHelper.sendData("RECEIVERS", serverData.getName(), "HelpOP-Request", player, message);
                }
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst attempting to handle plugin message", e);
        }
    }

}
