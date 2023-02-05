package me.blurmit.basics.command.defined;

import javafx.util.Pair;
import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.punishments.PunishmentType;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.RankUtil;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class KickCommand extends CommandBase {

    private final Basics plugin;

    public KickCommand(Basics plugin) {
        super(plugin.getName());
        setName("kick");
        setDescription("Kicks a player from the server");
        setUsage("/kick [-s] <player> <reason>");
        setPermission("basics.command.kick");

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

            if (target == null) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_NOT_FOUND + "", true, args[0]));
                return;
            }

            String fancyName = RankUtil.getColoredName(target.getUniqueId());
            String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                target.kickPlayer(Placeholders.parsePlaceholder(Messages.KICK_ALERT + "", reason));
            });
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PUNISHMENT_MESSAGE + "", true, "kicked", fancyName, reason));

            plugin.getPunishmentManager().storeHistory(
                    PunishmentType.KICK,
                    target.getUniqueId(),
                    (sender instanceof Player) ? ((Player) sender).getUniqueId() : null,
                    System.currentTimeMillis(),
                    -1,
                    plugin.getConfigManager().getConfig().getString("Server-Name.Default-Value"),
                    reason
            );
            plugin.getPunishmentManager().broadcastPunishment(sender, fancyName, PunishmentType.KICK, reason, silent);
        });

        return true;
    }

}
