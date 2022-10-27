package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.placeholder.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SudoCommand extends CommandBase {

    private final Basics plugin;

    public SudoCommand(Basics plugin) {
        super(plugin.getName());
        setName("sudo");
        setDescription("Forces a command to be executed as another player");
        setUsage("/sudo <player> <command>");
        setPermission("basics.commands.sudo");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_NOT_FOUND + "", args[0]));
            return true;
        }

        sender.sendMessage(Placeholders.parsePlaceholder(Messages.SUDO_SUCCESS + "", target, this, args));

        String command = Placeholders.parsePlaceholder(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));

        if (command.startsWith("c:")) {
            command = command.replaceFirst("c:", "");
            target.chat(command);
        } else {
            target.performCommand(command);
        }

        return true;

    }

}
