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

public class UnbanCommand extends CommandBase {

    private final Basics plugin;

    public UnbanCommand(Basics plugin) {
        super(plugin.getName());
        setName("unban");
        setDescription("Unbans a player");
        setUsage("/unban [-s] <player> <reason>");
        setPermission("basics.command.unban");

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
                targetName = target.getName();
                uuid = target.getUniqueId();
            } else {
                uuid = UUIDs.synchronouslyRetrieveUUID(finalArgs[0]);
                targetName = UUIDs.synchronouslyGetNameFromUUID(uuid);
            }

            if (!plugin.getPunishmentManager().isBanned(uuid)) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.NOT_BANNED + "", true, finalArgs[0]));
                return;
            }

            targetName = plugin.getRankManager().getHighestRankByPriority(uuid).getColor() + targetName;
            String reason = String.join(" ", Arrays.copyOfRange(finalArgs, 1, finalArgs.length));

            plugin.getPunishmentManager().getBannedPlayers().remove(uuid);
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PUNISHMENT_MESSAGE + "", true, "unbanned", targetName, reason));

            plugin.getPunishmentManager().storeUnban(
                    uuid,
                    (sender instanceof Player) ? ((Player) sender).getUniqueId() : null,
                    reason
            );
            plugin.getPunishmentManager().broadcastPardon(sender, targetName, PunishmentType.UNBAN, reason, silent.get());
        });

        return true;
    }

}
