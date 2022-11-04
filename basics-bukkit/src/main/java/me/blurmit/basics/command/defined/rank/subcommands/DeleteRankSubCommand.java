package me.blurmit.basics.command.defined.rank.subcommands;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.defined.SubCommand;
import me.blurmit.basics.util.lang.Messages;
import me.blurmit.basics.util.placeholder.Placeholders;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DeleteRankSubCommand extends SubCommand {

    private final Basics plugin;

    public DeleteRankSubCommand(Basics plugin, Command command) {
        super(plugin.getName(), command);
        setName("delete");
        setUsage("/rank delete <name>");
        setPermission("basics.commands.rank.delete");
        setDescription("Deletes a rank");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, Command command, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS_SUBCOMMAND + "", sender, this, args));
            return;
        }

        if (plugin.getRankManager().getRankByName(args[1].toLowerCase()) == null) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.RANK_NOT_FOUND + "", args[1].toLowerCase()));
            return;
        }

        plugin.getRankManager().deleteRank(args[1].toLowerCase());
        sender.sendMessage(Placeholders.parsePlaceholder(Messages.RANK_DELETED + "", args[1].toLowerCase()));
    }

}
