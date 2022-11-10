package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.lang.Messages;
import me.blurmit.basics.util.placeholder.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PingCommand extends CommandBase {

    private final Basics plugin;

    public PingCommand(Basics plugin) {
        super(plugin.getName());
        setName("ping");
        setDescription("Get the ping of yourself or another player");
        setUsage("/ping [player]");
        setPermission("basics.command.ping");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + ""));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.ONLY_PLAYERS + ""));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PING_MESSAGE + "", (Player) sender, this, args));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PING_MESSAGE + "", (Player) sender, this, args));
            return true;
        }

        sender.sendMessage(Placeholders.parsePlaceholder(Messages.PING_MESSAGE + "", target, this, args));
        return true;

    }

}
