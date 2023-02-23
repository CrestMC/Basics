package me.blurmit.basics.command.defined.punishment;

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

public class BlacklistCommand extends PunishmentCommand {

    private final Basics plugin;

    public BlacklistCommand(Basics plugin) {
        super(plugin);
        setName("blacklist");
        setDescription("IP bans a player from the server");
        setUsage("/blacklist [-s] <player> <reason>");
        setPermission("basics.command.blacklist");
        setAliases(Arrays.asList("ipban", "ban-ip", "ip-ban", "bl"));

        this.plugin = plugin;
    }

    @Override
    public void punish(CommandSender moderator, UUID target, String command, String[] args, boolean isSilent, long expiresAt) {
        String blacklistMessage;
        PunishmentType blacklistType;
        if (getExpiresAt() == -1) {
            blacklistMessage = Messages.BLACKLIST_PERMANENT_ALERT + "";
            blacklistType = PunishmentType.PERM_BLACKLIST;
        } else {
            blacklistMessage = Messages.BLACKLIST_TEMPORARY_ALERT + "";
            blacklistType = PunishmentType.TEMP_BLACKLIST;
        }

        PluginMessageUtil.sendData("BungeeCord", "KickPlayer", getTargetName(), Placeholders.parsePlaceholder(
                blacklistMessage,
                true,
                getReason(),
                getExpiresInText()
        ));

        moderator.sendMessage(Placeholders.parsePlaceholder(
                Messages.PUNISHMENT_MESSAGE + "",
                true,
                "blacklisted",
                getTargetName(),
                getReason(),
                getDurationText()
        ));

        // TODO: Store IPs in some sort of player data table so we can retrieve them for blacklists
        plugin.getPunishmentManager().storeBlacklist(
                getTargetUUID(),
                getModUUID(),
                getExpiresAt(),
                getServerName(),
                getReason(),
                "0.0.0.0"
        );
        plugin.getPunishmentManager().broadcastPunishment(
                moderator,
                getFancyTargetName(),
                blacklistType,
                getReason(),
                getDurationText(),
                isSilent()
        );
    }

}
