package dev.blurmit.basics.command.defined;

import dev.blurmit.basics.Basics;
import dev.blurmit.basics.command.CommandBase;
import dev.blurmit.basics.util.Placeholders;
import dev.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class EchoCommand extends CommandBase {

    private final Basics plugin;

    public EchoCommand(Basics plugin) {
        super(plugin.getName());
        setName("echo");
        setDescription("Echo a message back to the sender");
        setUsage("Usage: /echo <message>");
        setPermission("basics.commands.echo");

        this.plugin = plugin;
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

        String message = Placeholders.parsePlaceholder(String.join(" ", Arrays.copyOfRange(args, 0, args.length)), (Player) sender, this, args);
        sender.sendMessage(message);

        return true;
    }

}
