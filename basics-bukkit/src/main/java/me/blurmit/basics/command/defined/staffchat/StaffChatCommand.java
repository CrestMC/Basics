package me.blurmit.basics.command.defined.staffchat;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.PluginMessageUtil;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class StaffChatCommand extends CommandBase {

    private final Basics plugin;
    private final Set<UUID> usersChatToggled;

    public StaffChatCommand(Basics plugin) {
        super(plugin.getName());
        setName("staffchat");
        setDescription("Sends a global message to all online staff");
        setUsage("/staffchat <message>");
        setAliases(Collections.singletonList("sc"));
        setPermission("basics.command.staffchat");

        this.plugin = plugin;
        this.usersChatToggled = new HashSet<>();

        plugin.getServer().getPluginManager().registerEvents(new StaffChatListener(plugin, this), plugin);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", new StaffChatListener(plugin, this));
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parse(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parse(Messages.ONLY_PLAYERS + "", sender, this, args));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Placeholders.parse(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        Player player = (Player) sender;

        String server = Placeholders.parse("{server-name}");
        String message = Placeholders.parse(String.join(" ", Arrays.copyOfRange(args, 0, args.length)), player, this, args);
        String format = Placeholders.parse(plugin.getConfigManager().getConfig().getString("StaffChat.Format"), player, this, null, null, false, server, player.getName(), message);

        PluginMessageUtil.sendData("BungeeCord", "Staff", "Chat", server, player.getName(), message);
        plugin.getServer().broadcast(format, "basics.staffchat");
        return true;
    }

}
