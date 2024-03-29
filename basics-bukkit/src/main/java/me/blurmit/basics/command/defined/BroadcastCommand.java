package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class BroadcastCommand extends CommandBase {

    private final Basics plugin;

    public BroadcastCommand(Basics plugin) {
        super(plugin.getName());
        setName("broadcast");
        setDescription("Broadcasts a message to the entire server");
        setAliases(Arrays.asList("alert", "bc"));
        setUsage("/broadcast <message>");
        setPermission("basics.command.broadcast");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parse(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Placeholders.parse(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        plugin.getServer().broadcastMessage(Placeholders.parse(Messages.BROADCAST_MESSAGE + "", message));

        return true;
    }

}
