package me.blurmit.basics.command.defined.rank.subcommands;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.defined.SubCommand;
import me.blurmit.basics.rank.Rank;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.UUIDUtil;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RevokeRankSubCommand extends SubCommand {

    private final Basics plugin;

    public RevokeRankSubCommand(Basics plugin, Command command) {
        super(plugin.getName(), command);
        setName("revoke");
        setUsage("/rank revoke <rank> <player>");
        setPermission("basics.command.rank.revoke");
        setDescription("Removes a rank from a player");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, Command command, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Placeholders.parse(Messages.INVALID_ARGS_SUBCOMMAND + "", sender, this, args));
            return;
        }

        UUIDUtil.asyncGetUUID(args[2].toLowerCase(), uuid -> handleRevokeRank(sender, args, uuid));
    }

    private void handleRevokeRank(CommandSender sender, String[] args, UUID uuid) {
        Player target = plugin.getServer().getPlayer(args[2]);
        Rank rank = plugin.getRankManager().getRankByName(args[1]);

        if (uuid == null) {
            sender.sendMessage(Placeholders.parse(Messages.ACCOUNT_DOESNT_EXIST + "", true, args[2]));
            return;
        }

        if (rank == null) {
            sender.sendMessage(Placeholders.parse(Messages.RANK_NOT_FOUND + "", true, args[1].toLowerCase()));
            return;
        }

        plugin.getRankManager().hasRank(uuid, rank.getName(), hasRank -> {
            if (!hasRank) {
                sender.sendMessage(Placeholders.parse(Messages.RANK_NOT_OWNED + "", true, rank.getDisplayName()));
                return;
            }

            plugin.getRankManager().revokeRank(rank.getName(), uuid.toString());
            sender.sendMessage(Placeholders.parse(Messages.RANK_REVOKED_SUCCESS + "", true, args[2], rank.getDisplayName()));

            if (target != null) {
                target.sendMessage(Placeholders.parse(Messages.RANK_REVOKED + "", true, rank.getDisplayName()));
            }
        });
    }

}
