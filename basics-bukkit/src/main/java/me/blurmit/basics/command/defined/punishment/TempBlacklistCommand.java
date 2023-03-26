package me.blurmit.basics.command.defined.punishment;

import me.blurmit.basics.Basics;
import me.blurmit.basics.punishments.PunishmentType;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.PluginMessageUtil;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.UUID;

public class TempBlacklistCommand extends PunishmentCommand {

    private final Basics plugin;

    public TempBlacklistCommand(Basics plugin) {
        super(plugin);
        setName("tempblacklist");
        setDescription("Temporarily blacklists a player from the server");
        setUsage("/tempblacklist [-s] <player> [duration] <reason>");
        setPermission("basics.command.tempblacklist");
        setAliases(Arrays.asList("tempipban", "tempban-ip", "tempip-ban", "bl"));

        this.plugin = plugin;
    }

    @Override
    public void punish(CommandSender moderator, UUID target, String command, String[] args, boolean isSilent, long expiresAt) {
        if (getExpiresAt() == -1) {
            moderator.sendMessage(Placeholders.parse(Messages.PUNISHMENT_NO_TIME_PROVIDED + ""));
            return;
        }

        PluginMessageUtil.sendData("BungeeCord", "KickPlayer", getTargetName(), Placeholders.parse(
                Messages.BLACKLIST_TEMPORARY_ALERT + "", true, getReason(), getExpiresInText()
        ));

        moderator.sendMessage(Placeholders.parse(
                Messages.PUNISHMENT_MESSAGE + "", true, "blacklisted", getTargetName(), getReason(), getDurationText()
        ));

        plugin.getPunishmentManager().storeBlacklist(getTargetUUID(), getModUUID(), getExpiresAt(), getReason());
        plugin.getPunishmentManager().broadcastPunishment(moderator, getFancyTargetName(), PunishmentType.TEMP_BLACKLIST, getReason(), getDurationText(), isSilent());
    }

}
