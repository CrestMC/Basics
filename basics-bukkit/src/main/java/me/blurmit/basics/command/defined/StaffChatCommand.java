package me.blurmit.basics.command.defined;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.placeholder.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import me.blurmit.basics.util.pluginmessage.PluginMessageHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;

public class StaffChatCommand extends CommandBase implements Listener, PluginMessageListener {

    private final Basics plugin;

    public StaffChatCommand(Basics plugin) {
        super(plugin.getName());
        setName("staffchat");
        setDescription("Sends a global message to all online staff");
        setUsage("/staffchat <message>");
        setAliases(Collections.singletonList("sc"));
        setPermission("basics.commands.staffchat");

        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.ONLY_PLAYERS + "", sender, this, args));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        Player player = (Player) sender;

        // Yes, I used my placeholder system for the server name.
        String server = Placeholders.parsePlaceholder("{server-name}");
        String message = Placeholders.parsePlaceholder(String.join(" ", Arrays.copyOfRange(args, 0, args.length)), player, this, args);
        String format = Placeholders.parsePlaceholder(plugin.getConfigManager().getConfig().getString("StaffChat.Format"), server, player.getName(), message);

        PluginMessageHelper.sendData("BungeeCord", "Staff-Chat", server, player.getName(), message);
        plugin.getServer().broadcast(format, "basics.staffchat");
        return true;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().hasPermission("basics.staffchat")) {
            return;
        }

        String prefix = plugin.getConfigManager().getConfig().getString("StaffChat.Prefix") + " ";

        if (prefix.equalsIgnoreCase("none")) {
            return;
        }

        if (!event.getMessage().startsWith(prefix)) {
            return;
        }

        event.setCancelled(true);
        event.setMessage(event.getMessage().replaceFirst(prefix, ""));

        String server = Placeholders.parsePlaceholder("{server-name}", true);
        String format = Placeholders.parsePlaceholder(plugin.getConfigManager().getConfig().getString("StaffChat.Format"), true, server, event.getPlayer().getName(), event.getMessage());

        PluginMessageHelper.sendData("BungeeCord", "Staff-Chat", server, event.getPlayer().getName(), event.getMessage());
        plugin.getServer().broadcast(format, "basics.staffchat");
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        String subchannel = input.readUTF();

        switch (subchannel) {
            case "Staff-Chat": {
                String server = input.readUTF();
                String playerName = input.readUTF();
                String msg = input.readUTF();
                String format = Placeholders.parsePlaceholder(plugin.getConfigManager().getConfig().getString("StaffChat.Format"), server, playerName, msg);
                plugin.getServer().broadcast(format, "basics.staffchat");
                break;
            }
            case "Staff-Connected": {
                String server = input.readUTF();
                String playerName = input.readUTF();
                String format = Placeholders.parsePlaceholder(plugin.getConfigManager().getConfig().getString("StaffChat.Connect"), playerName, server);
                plugin.getServer().broadcast(format, "basics.staffchat");
                break;
            }
            case "Staff-Disconnected": {
                String server = input.readUTF();
                String playerName = input.readUTF();
                String format = Placeholders.parsePlaceholder(plugin.getConfigManager().getConfig().getString("StaffChat.Disconnect"), playerName, server);
                plugin.getServer().broadcast(format, "basics.staffchat");
                break;
            }
            case "Staff-ServerSwitch": {
                String playerName = input.readUTF();
                String originalServer = input.readUTF();
                String newServer = input.readUTF();
                String format = Placeholders.parsePlaceholder(plugin.getConfigManager().getConfig().getString("StaffChat.Switch"), playerName, newServer, originalServer);
                plugin.getServer().broadcast(format, "basics.staffchat");
            }
        }
    }

}