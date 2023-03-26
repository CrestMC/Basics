package me.blurmit.basics.command.defined.rank.subcommands;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.defined.SubCommand;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CreateRankSubCommand extends SubCommand {

    private final Basics plugin;

    public CreateRankSubCommand(Basics plugin, Command command) {
        super(plugin.getName(), command);
        setName("create");
        setUsage("/rank create <name>");
        setPermission("basics.command.rank.create");
        setDescription("Creates a new rank");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, Command command, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Placeholders.parse(Messages.INVALID_ARGS_SUBCOMMAND + "", sender, this, args));
            return;
        }

        if (plugin.getRankManager().getRankByName(args[1].toLowerCase()) != null) {
            sender.sendMessage(Placeholders.parse(Messages.RANK_ALREADY_EXISTS + "", sender, this, args));
            return;
        }

        plugin.getRankManager().createRank(args[1].toLowerCase());
        sender.sendMessage(Placeholders.parse(Messages.RANK_CREATED + "", args[1].toLowerCase()));
    }

}
