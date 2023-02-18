package me.blurmit.basics.command.defined;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.PluginMessageUtil;
import me.blurmit.basics.util.RankUtil;
import me.blurmit.basics.util.UUIDUtil;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class HelpopCommand extends CommandBase implements PluginMessageListener {

    private final Basics plugin;

    public HelpopCommand(Basics plugin) {
        super(plugin.getName());
        setName("helpop");
        setAliases(Arrays.asList("helpme", "messagestaff"));
        setDescription("Send a message to all active staff members.");
        setPermission("basics.command.helpop");
        setCooldown(10);
        setUsage("/helpop <message>");

        this.plugin = plugin;

        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
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
        String message = String.join(" ", Arrays.copyOfRange(args, 0, args.length));

        player.sendMessage(Placeholders.parsePlaceholder(Messages.HELPOP_SUBMITTED + ""));

        plugin.getServer().broadcast(
                Placeholders.parsePlaceholder(Messages.HELPOP_REQUEST + "", true, RankUtil.getColor(player.getUniqueId()) + player.getName(), ChatColor.stripColor(message)),
                "basics.helpop.read"
        );

        PluginMessageUtil.sendData("BungeeCord", "HelpOP-Request", player.getName(), ChatColor.stripColor(message));
        return true;
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        String subchannel = input.readUTF();

        if (!subchannel.equals("HelpOP-Request")) {
            return;
        }

        String user = input.readUTF();
        String request = input.readUTF();

        UUIDUtil.asyncGetUUID(user, uuid -> {
            plugin.getServer().broadcast(
                    Placeholders.parsePlaceholder(Messages.HELPOP_REQUEST + "", true, RankUtil.getColor(uuid) + user, ChatColor.stripColor(request)),
                    "basics.helpop.read"
            );
        });
    }

}
