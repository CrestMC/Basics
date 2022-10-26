package me.blurmit.basics.placeholder;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.blurmit.basics.Basics;
import me.blurmit.basics.events.PlaceholderRequestEvent;
import me.blurmit.basics.util.pluginmessage.PluginMessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ServerPlaceholder implements Listener, PluginMessageListener {

    private final Basics plugin;
    private final Map<String, String> playerCountMap;
    private String currentServerName;
    private BukkitTask serverNameRetriever;

    public ServerPlaceholder(Basics plugin) {
        this.plugin = plugin;
        this.playerCountMap = new HashMap<>();
        this.currentServerName = plugin.getConfigManager().getConfig().getString("Server-Name.Default-Value");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);

        requestOnlineServers();

        if (plugin.getConfigManager().getConfig().getBoolean("Server-Name.Auto-Detect")) {
            requestServerName();
        }
    }

    private void requestOnlineServers() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> PluginMessageHelper.sendData("BungeeCord", "ServerStatus"), 0L, 20 * 3L);
    }

    private void requestServerName() {
        serverNameRetriever = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (currentServerName.equals("Unknown")) {
                PluginMessageHelper.sendData("BungeeCord", "GetServer");
            } else {
                serverNameRetriever.cancel();
            }
        }, 0L, 20 * 3L);
    }

    @EventHandler
    public void onPlaceholderRequest(PlaceholderRequestEvent event) {
        String placeholder = event.getPlaceholder().toLowerCase();

        if (placeholder.startsWith("playercount-")) {
            try {
                String serverName = placeholder.replace("playercount-", "").toLowerCase();
                event.setResponse(playerCountMap.get(serverName) == null ? ChatColor.RED + "Offline" : playerCountMap.get(serverName));
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equalsIgnoreCase("server-name")) {
            try {
                event.setResponse(currentServerName);
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equalsIgnoreCase("server-website")) {
            String websiteLink = plugin.getConfigManager().getConfig().getString("Social-Media.Website");
            event.setResponse(websiteLink);
        }

        if (placeholder.equalsIgnoreCase("server-discord")) {
            String discordLink = plugin.getConfigManager().getConfig().getString("Social-Media.Discord");
            event.setResponse(discordLink);
        }

        if (placeholder.equalsIgnoreCase("server-store")) {
            String storeLink = plugin.getConfigManager().getConfig().getString("Social-Media.Store");
            event.setResponse(storeLink);
        }

        if (placeholder.equalsIgnoreCase("server-twitter")) {
            String twitterLink = plugin.getConfigManager().getConfig().getString("Social-Media.Twitter");
            event.setResponse(twitterLink);
        }
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        String subchannel = input.readUTF();

        switch (subchannel) {
            case "ServerStatus": {
                String data = input.readUTF();

                Set<String> servers = new HashSet<>(Arrays.asList(data.split(", ")));
                servers.forEach(server -> {
                    String name = server.split(":")[0].toLowerCase();
                    String status = server.split(":")[1];

                    if (playerCountMap.containsKey(name)) {
                        playerCountMap.replace(name, status);
                        return;
                    }

                    playerCountMap.put(name, status);
                });

                break;
            }
            case "GetServer": {
                currentServerName = input.readUTF();
            }
        }
    }
}
