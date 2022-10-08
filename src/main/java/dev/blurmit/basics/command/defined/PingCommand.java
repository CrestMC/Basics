package dev.blurmit.basics.command.defined;

import dev.blurmit.basics.Basics;
import dev.blurmit.basics.command.CommandBase;
import dev.blurmit.basics.util.Placeholders;
import dev.blurmit.basics.util.lang.Messages;
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
        setUsage("Usage: /ping [player]");
        setPermission("basics.commands.ping");

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

        sender.sendMessage(Placeholders.parsePlaceholder(Messages.PING_MESSAGE + "", target, false, target.getPing()));
        return true;

    }

}
