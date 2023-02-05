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

            String fancyName = RankUtil.getColoredName(uuid);
            String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            if (target != null) {
                target.sendMessage(Placeholders.parsePlaceholder(Messages.MUTE_PERMANENT_ALERT + "", true, reason, "never"));
            }

            plugin.getPunishmentManager().getMutedPlayers().add(uuid);
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PUNISHMENT_MESSAGE + "", true, "muted", fancyName, reason));

            plugin.getPunishmentManager().storeMute(
                    uuid,
                    (sender instanceof Player) ? ((Player) sender).getUniqueId() : null,
                    -1,
                    plugin.getConfigManager().getConfig().getString("Server-Name.Default-Value"),
                    reason
            );
            plugin.getPunishmentManager().broadcastPunishment(sender, fancyName, PunishmentType.PERM_MUTE, reason, silent);
        });

        return true;
    }

}
