package me.blurmit.basics.command.defined.staffchat;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.blurmit.basics.Basics;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.PluginMessageUtil;
import me.blurmit.basics.util.RankUtil;
import me.blurmit.basics.util.UUIDUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StaffChatListener implements Listener, PluginMessageListener {

    private final Basics plugin;
    private final StaffChatCommand command;
    private final FileConfiguration config;

    public StaffChatListener(Basics plugin, StaffChatCommand command) {
        this.plugin = plugin;
        this.command = command;
        this.config = plugin.getConfigManager().getConfig();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().hasPermission("basics.staffchat")) {
            return;
        }

        Player player = event.getPlayer();
        String prefix = config.getString("StaffChat.Prefix");

        if (prefix.equalsIgnoreCase("none")) {
            return;
        }

        if (!event.getMessage().startsWith(prefix)) {
            return;
        }

        if (event.getMessage().startsWith(prefix + " ")) {
            prefix += " ";
        }

        event.setCancelled(true);
        event.setMessage(event.getMessage().replaceFirst(prefix, ""));

        String message = event.getMessage();
        String server = Placeholders.parse("{server-name}", true);
        String format = Placeholders.parse(config.getString("StaffChat.Format"), player, command, null, null, event.isAsynchronous(), server, player.getName(), message);

        PluginMessageUtil.sendData("BungeeCord", "Staff", "Chat", server, player.getName(), message);
        plugin.getServer().broadcast(format, "basics.staffchat");
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(message);

        if (!input.readUTF().equals("Staff")) {
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String subchannel = input.readUTF();

            String server = input.readUTF();
            String playerName = input.readUTF();

            UUID uuid = UUIDUtil.getUUID(playerName);
            String coloredName = RankUtil.getColoredName(uuid);
            String format;

            switch (subchannel) {
                case "Chat": {
                    String msg = input.readUTF();
                    format = Placeholders.parse(config.getString("StaffChat.Format"), true, server, coloredName, msg);
                    break;
                }
                case "Connected": {
                    format = Placeholders.parse(config.getString("StaffChat.Connect"), true, coloredName, server);
                    break;
                }
                case "Disconnected": {
                    format = Placeholders.parse(config.getString("StaffChat.Disconnect"), true, coloredName, server);
                    break;
                }
                case "ServerSwitch": {
                    String newServer = input.readUTF();
                    format = Placeholders.parse(config.getString("StaffChat.Switch"), true, coloredName, newServer, server);
                    break;
                }
                default: {
                    return;
                }
            }

            plugin.getServer().broadcast(format, "basics.staffchat");
        });
    }

}
