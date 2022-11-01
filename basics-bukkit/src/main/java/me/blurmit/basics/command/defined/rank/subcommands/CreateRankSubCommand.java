package me.blurmit.basics.command.defined.rank.subcommands;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.defined.SubCommand;
import me.blurmit.basics.util.placeholder.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CreateRankSubCommand extends SubCommand {

    private final Basics plugin;

    public CreateRankSubCommand(Basics plugin, Command command) {
        super(plugin.getName(), command);
        setName("create");
        setUsage("/rank create <name>");
        setPermission("basics.commands.rank.create");
        setDescription("Creates a new rank");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, Command command, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS_SUBCOMMAND + "", sender, this, args));
            return;
        }

        if (plugin.getRankManager().getRankByName(args[1].toLowerCase()) != null) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.RANK_ALREADY_EXISTS + "", sender, this, args));
            return;
        }

        plugin.getRankManager().createRank(args[1].toLowerCase());
        sender.sendMessage(Placeholders.parsePlaceholder(Messages.RANK_CREATED + "", args[1].toLowerCase()));
    }

}
