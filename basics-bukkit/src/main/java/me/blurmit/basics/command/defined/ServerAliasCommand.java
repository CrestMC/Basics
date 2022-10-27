package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.placeholder.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import me.blurmit.basics.util.pluginmessage.PluginMessageHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ServerAliasCommand extends CommandBase {

    private final Basics plugin;

    public ServerAliasCommand(Basics plugin) {
        super(plugin.getName());
        setName("connect");
        setDescription("Connect to another server");
        setUsage("/connect <server>");
        setPermission("basics.commands.connect");

        List<String> aliases = new ArrayList<>();
        plugin.getConfigManager().getConfig().getConfigurationSection("Server-Aliases").getValues(false).keySet().forEach(value -> aliases.add(value + ""));
        setAliases(aliases);

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.ONLY_PLAYERS + "", sender, this, args));
            return true;
        }

        if ("connect".equalsIgnoreCase(commandLabel)) {
            if (!sender.hasPermission(getPermission())) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
                return true;
            }

            sender.sendMessage(Placeholders.parsePlaceholder(Messages.CONNECT_MESSAGE + "", args[0]));
            PluginMessageHelper.sendData("BungeeCord", "ConnectOther", sender.getName(), args[0]);
        } else {
            String server = plugin.getConfigManager().getConfig().getConfigurationSection("Server-Aliases").getString(commandLabel);
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.CONNECT_MESSAGE + "", server));
            PluginMessageHelper.sendData("BungeeCord", "ConnectOther", sender.getName(), server);
        }

        return true;
    }

}
