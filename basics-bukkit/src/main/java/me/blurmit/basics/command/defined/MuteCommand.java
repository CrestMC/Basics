package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.punishments.PunishmentType;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.UUIDs;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MuteCommand extends CommandBase {

    private final Basics plugin;

    public MuteCommand(Basics plugin) {
        super(plugin.getName());
        setName("mute");
        setDescription("Prevents a player from speaking in chat");
        setUsage("/mute [-s] <player> <reason>");
        setPermission("basics.command.mute");

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

        if (finalArgs.length < 2) {
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

                if (uuid == null) {
                    sender.sendMessage(Placeholders.parsePlaceholder(Messages.ACCOUNT_DOESNT_EXIST + "", true, finalArgs[0]));
                    return;
                }
            }

            if (target != null) {
                targetName = target.getName();
            }  else {
                targetName = UUIDs.synchronouslyGetNameFromUUID(uuid);
            }

            targetName = plugin.getRankManager().getHighestRankByPriority(uuid).getColor() + targetName;
            String reason = String.join(" ", Arrays.copyOfRange(finalArgs, 1, finalArgs.length));

            if (target != null) {
                target.sendMessage(Placeholders.parsePlaceholder(Messages.MUTE_PERMANENT_ALERT + "", true, reason, "never"));
            }

            plugin.getPunishmentManager().getMutedPlayers().add(uuid);
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PUNISHMENT_MESSAGE + "", true, "muted", targetName, reason));

            plugin.getPunishmentManager().storeMute(
                    uuid,
                    (sender instanceof Player) ? ((Player) sender).getUniqueId() : null,
                    -1,
                    plugin.getConfigManager().getConfig().getString("Server-Name.Default-Value"),
                    reason
            );
            plugin.getPunishmentManager().broadcastPunishment(sender, targetName, PunishmentType.PERM_MUTE, reason, silent.get());
        });

        return true;
    }

}
