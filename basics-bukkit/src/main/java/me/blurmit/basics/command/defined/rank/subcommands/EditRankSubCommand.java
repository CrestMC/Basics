package me.blurmit.basics.command.defined.rank.subcommands;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.defined.SubCommand;
import me.blurmit.basics.rank.Rank;
import me.blurmit.basics.util.placeholder.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class EditRankSubCommand extends SubCommand {

    private final Basics plugin;

    public EditRankSubCommand(Basics plugin, Command command) {
        super(plugin.getName(), command);
        setName("edit");
        setUsage("/rank edit <rank>");
        setPermission("basics.commands.rank.delete");
        setDescription("Edits a rank");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, Command command, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS_SUBCOMMAND + "", sender, this, args));
            return;
        }

        String rankName = args[1].toLowerCase();
        Rank rank = plugin.getRankManager().getRankByName(rankName);

        if (rank == null) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.RANK_NOT_FOUND + "", rankName));
            return;
        }

        switch (args[2].toLowerCase()) {
            case "addpermission": {
                boolean negated;
                String server;

                try {
                    negated = Boolean.parseBoolean(args[4]);
                } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
                    negated = false;
                }

                try {
                    server = args[5];
                } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
                    server = "global";
                }

                plugin.getRankManager().giveRankPermission(rank.getName(), args[3], server, negated);
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.RANK_PERMISSION_ADDED + "", args[3], rank.getDisplayName()));
                return;
            }
            case "removepermission": {
                plugin.getRankManager().removeRankPermission(rank.getName(), args[3]);
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.RANK_PERMISSION_REMOVED + "", args[3], rank.getDisplayName()));
                return;
            }
            case "setprefix": {
                String prefix = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

                plugin.getRankManager().setRankPrefix(rank.getName(), prefix);
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.RANK_PREFIX_SET + "", prefix, rank.getDisplayName()));
                return;
            }
            case "setsuffix": {
                String suffix = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

                plugin.getRankManager().setRankSuffix(rank.getName(), suffix);
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.RANK_SUFFIX_SET + "", suffix, rank.getDisplayName()));
                return;
            }
            case "setdefault": {
                boolean isDefault = Boolean.parseBoolean(args[3]);

                plugin.getRankManager().setRankDefault(rank.getName(), isDefault);
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.RANK_DEFAULT_SET + "", rank.getDisplayName(), isDefault ? "now" : "no longer"));
                return;
            }
            case "setdisplayname": {
                String displayName = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

                plugin.getRankManager().setDisplayName(rank.getName(), displayName);
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.RANK_DISPLAYNAME_SET + "", displayName, rank.getName()));
                return;
            }
            case "setpriority": {
                int priority;

                try {
                    priority = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(Placeholders.parsePlaceholder(Messages.NUMBER_INVALID + "", args[3]));
                    return;
                }

                plugin.getRankManager().setRankPriority(rank.getName(), priority);
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.RANK_PRIORITY_SET + "", priority + "", rank.getName()));
                return;
            }
            default: {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS_SUBCOMMAND + "", sender, this, args));
            }
        }
    }

}
