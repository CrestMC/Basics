package me.blurmit.basics.command.defined;

import javafx.util.Pair;
import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.punishments.PunishmentType;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.RankUtil;
import me.blurmit.basics.util.UUIDUtil;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;

public class UnblacklistCommand extends CommandBase {

    private final Basics plugin;

    public UnblacklistCommand(Basics plugin) {
        super(plugin.getName());
        setName("unblacklist");
        setDescription("Unblacklists a player");
        setUsage("/unblacklist [-s] <player> <reason>");
        setPermission("basics.command.unblacklist");
        setAliases(Arrays.asList("unipban", "pardonip", "unipban", "removeipban"));

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] arguments) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, arguments));
            return true;
        }

        Pair<Boolean, String[]> silentPair = plugin.getPunishmentManager().isSilent(arguments);
        boolean silent = silentPair.getKey();
        String[] args = silentPair.getValue();

        if (args.length < 2) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Player target = plugin.getServer().getPlayer(args[0]);
            UUID uuid;

            if (target != null) {
                uuid = target.getUniqueId();
            } else {
                uuid = UUIDUtil.getUUID(args[0]);

                if (uuid == null) {
                    sender.sendMessage(Placeholders.parsePlaceholder(Messages.ACCOUNT_DOESNT_EXIST + "", true, args[0]));
                    return;
                }
            }

            if (!plugin.getPunishmentManager().isBlacklisted(uuid)) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.NOT_BLACKLISTED + "", true, args[0]));
                return;
            }

            String fancyName = RankUtil.getColoredName(uuid);
            String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            plugin.getPunishmentManager().getBannedPlayers().remove(uuid);
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PUNISHMENT_MESSAGE + "", true, "unblacklisted", fancyName, reason));

            plugin.getPunishmentManager().storeUnblacklist(
                    "0.0.0.0",
                    (sender instanceof Player) ? ((Player) sender).getUniqueId() : null,
                    reason
            );
            plugin.getPunishmentManager().broadcastPardon(sender, fancyName, PunishmentType.UNBLACKLIST, reason, silent);
        });

        return true;
    }

}
