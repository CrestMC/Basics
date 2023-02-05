package me.blurmit.basics.command.defined;

import javafx.util.Pair;
import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.punishments.PunishmentType;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.PluginMessageUtil;
import me.blurmit.basics.util.RankUtil;
import me.blurmit.basics.util.UUIDUtil;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;

public class BlacklistCommand extends CommandBase {

    private final Basics plugin;

    public BlacklistCommand(Basics plugin) {
        super(plugin.getName());
        setName("blacklist");
        setDescription("IP bans a player from the server");
        setUsage("/blacklist [-s] <player> <reason>");
        setPermission("basics.command.blacklist");
        setAliases(Arrays.asList("ipban", "ban-ip", "ip-ban", "bl"));

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
            String name;
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

            if (target != null) {
                name = target.getName();
            }  else {
                name = UUIDUtil.getName(uuid);
            }

            String fancyName = RankUtil.getColoredName(uuid);
            String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            PluginMessageUtil.sendData("BungeeCord", "KickPlayer", name, Placeholders.parsePlaceholder(Messages.BLACKLIST_PERMANENT_ALERT + "", true, reason, "never"));

            if (target != null) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    target.kickPlayer(Placeholders.parsePlaceholder(Messages.BLACKLIST_PERMANENT_ALERT + "", reason, "never"));
                });
            }
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PUNISHMENT_MESSAGE + "", true, "blacklisted", fancyName, reason));

            plugin.getPunishmentManager().storeBlacklist(
                    uuid,
                    (sender instanceof Player) ? ((Player) sender).getUniqueId() : null,
                    -1,
                    plugin.getConfigManager().getConfig().getString("Server-Name.Default-Value"),
                    reason,
                    "0.0.0.0"
            );
            plugin.getPunishmentManager().broadcastPunishment(sender, fancyName, PunishmentType.PERM_BLACKLIST, reason, silent);
        });

        return true;
    }

}
