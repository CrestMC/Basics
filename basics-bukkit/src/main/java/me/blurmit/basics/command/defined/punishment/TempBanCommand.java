package me.blurmit.basics.command.defined.punishment;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.punishments.PunishmentType;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.PluginMessageUtil;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class TempBanCommand extends PunishmentCommand {

    private final Basics plugin;

    public TempBanCommand(Basics plugin) {
        super(plugin);
        setName("tempban");
        setDescription("Temporarily bans a player from the server");
        setUsage("/tempban [-s] <player> [duration] <reason>");
        setPermission("basics.command.tempban");

        this.plugin = plugin;
    }

    @Override
    public void punish(CommandSender moderator, UUID target, String command, String[] args, boolean isSilent, long expiresAt) {
        if (getExpiresAt() == -1) {
            moderator.sendMessage(Placeholders.parsePlaceholder(Messages.PUNISHMENT_NO_TIME_PROVIDED + ""));
            return;
        }

        PluginMessageUtil.sendData("BungeeCord", "KickPlayer", getTargetName(), Placeholders.parsePlaceholder(
                Messages.BAN_TEMPORARY_ALERT + "",
                true,
                getReason(),
                getExpiresInText()
        ));

        moderator.sendMessage(Placeholders.parsePlaceholder(
                Messages.PUNISHMENT_MESSAGE + "",
                true,
                "banned",
                getTargetName(),
                getReason(),
                getDurationText()
        ));

        plugin.getPunishmentManager().storeBan(
                getTargetUUID(),
                getModUUID(),
                getExpiresAt(),
                getServerName(),
                getReason()
        );
        plugin.getPunishmentManager().broadcastPunishment(
                moderator,
                getFancyTargetName(),
                PunishmentType.TEMP_BAN,
                getReason(),
                getDurationText(),
                isSilent()
        );
    }

}
