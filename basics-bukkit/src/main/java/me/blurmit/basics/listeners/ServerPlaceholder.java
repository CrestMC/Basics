package me.blurmit.basics.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.blurmit.basics.Basics;
import me.blurmit.basics.events.PlaceholderRequestEvent;
import me.blurmit.basics.util.PluginMessageUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ServerPlaceholder extends PlaceholderExpansion implements Listener, PluginMessageListener {

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
        sendWhitelistState();

        if (plugin.getConfigManager().getConfig().getBoolean("Server-Name.Auto-Detect")) {
            requestServerName();
        }
    }

    private void requestOnlineServers() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> PluginMessageUtil.sendData("BungeeCord", "ServerStatus"), 5L, 40L);
    }

    private void requestServerName() {
        serverNameRetriever = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (currentServerName.equals("Unknown")) {
                PluginMessageUtil.sendData("BungeeCord", "GetServer");
            } else {
                serverNameRetriever.cancel();
            }
        }, 0L, 20 * 3L);
    }

    private void sendWhitelistState() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> PluginMessageUtil.sendData("BungeeCord", "WhitelistStatus", plugin.getServer().hasWhitelist() + ""), 0L, 40L);
    }

    @EventHandler
    public void onPlaceholderRequest(PlaceholderRequestEvent event) {
        String placeholder = event.getPlaceholder().toLowerCase();

        if (placeholder.equalsIgnoreCase("server-name")) {
            try {
                event.setResponse(currentServerName);
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equalsIgnoreCase("server-playercount")) {
            try {
                event.setResponse(plugin.getServer().getOnlinePlayers().size() + "");
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equalsIgnoreCase("server-players-filtered")) {
            try {
                if (!(event.getSender() instanceof Player)) {
                    event.setResponse(plugin.getServer().getOnlinePlayers().size() + "");
                    return;
                }

                Player player = (Player) event.getSender();
                long players = plugin.getServer().getOnlinePlayers().stream().filter(player::canSee).count();
                event.setResponse(players + "");
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.startsWith("server-playercount-")) {
            try {
                String serverName = placeholder.replace("server-playercount-", "").toLowerCase();
                event.setResponse(playerCountMap.get(serverName) == null ? ChatColor.RED + "Offline" : playerCountMap.get(serverName));
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equalsIgnoreCase("server-max-players")) {
            try {
                event.setResponse(plugin.getServer().getMaxPlayers() + "");
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

                for (String server : servers) {
                    String name;
                    String status;

                    try {
                        name = server.split(":")[0].toLowerCase();
                        status = server.split(":")[1];
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                        continue;
                    }

                    if (playerCountMap.containsKey(name)) {
                        playerCountMap.replace(name, status);
                        continue;
                    }

                    playerCountMap.put(name, status);
                }

                break;
            }
            case "GetServer": {
                currentServerName = input.readUTF();
            }
        }
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.startsWith("server_playercount_")) {
            String serverName = params.replace("server_playercount_", "").toLowerCase();
            return playerCountMap.get(serverName) == null ? ChatColor.RED + "Offline" : playerCountMap.get(serverName);
        }

        return null;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "basics";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

}
