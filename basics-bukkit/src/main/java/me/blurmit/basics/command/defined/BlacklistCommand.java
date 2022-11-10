package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.punishments.PunishmentType;
import me.blurmit.basics.util.UUIDs;
import me.blurmit.basics.util.lang.Messages;
import me.blurmit.basics.util.placeholder.Placeholders;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class BlacklistCommand extends CommandBase {

    private final Basics plugin;

    public BlacklistCommand(Basics plugin) {
        super(plugin.getName());
        setName("blacklist");
        setDescription("IP bans a player from the server");
        setUsage("/blacklist [-s] <player> <reason>");
        setPermission("basics.command.blacklist");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        AtomicBoolean silent = new AtomicBoolean();
        AtomicReference<String[]> arguments = new AtomicReference<>();

        plugin.getPunishmentManager().isSilent(args, (isSilent, newArgs) -> {
            silent.set(isSilent);
            arguments.set(newArgs);
        });

        final String[] finalArgs = arguments.get();

        if (args.length < 2) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Player target = plugin.getServer().getPlayer(finalArgs[0]);
            String targetName;
            UUID uuid;

            if (target != null) {
                uuid = target.getUniqueId();
            } else {
                uuid = UUIDs.synchronouslyRetrieveUUID(finalArgs[0]);
            }

            if (uuid == null) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.ACCOUNT_DOESNT_EXIST + "", true, finalArgs[0]));
                return;
            }

            if (target != null) {
                targetName = target.getName();
            }  else {
                targetName = UUIDs.synchronouslyGetNameFromUUID(uuid);
            }

            targetName = plugin.getRankManager().getHighestRankByPriority(uuid).getColor() + targetName;
            String reason = String.join(" ", Arrays.copyOfRange(finalArgs, 1, finalArgs.length));

            if (target != null) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    target.kickPlayer(Placeholders.parsePlaceholder(Messages.BLACKLIST_PERMANENT_ALERT + "", reason, "never"));
                });
            }
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PUNISHMENT_MESSAGE + "", true, "blacklisted", targetName, reason));

            plugin.getPunishmentManager().storeBlacklist(
                    uuid,
                    (sender instanceof Player) ? ((Player) sender).getUniqueId() : null,
                    -1,
                    plugin.getConfigManager().getConfig().getString("Server-Name.Default-Value"),
                    reason,
                    "0.0.0.0"
            );
            plugin.getPunishmentManager().broadcastPunishment(sender, targetName, PunishmentType.PERM_BLACKLIST, reason, silent.get());
        });

        return true;
    }

}
