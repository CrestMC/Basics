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
import java.util.Collections;
import java.util.UUID;

public class BanCommand extends PunishmentCommand {

    private final Basics plugin;

    public BanCommand(Basics plugin) {
        super(plugin);
        setName("ban");
        setDescription("Bans a player from the server");
        setUsage("/ban [-s] <player> <reason>");
        setPermission("basics.command.ban");
        setAliases(Collections.singletonList("permban"));

        this.plugin = plugin;
    }

    @Override
    public void punish(CommandSender moderator, UUID target, String command, String[] args, boolean isSilent, long expiresAt) {
        String banMessage;
        PunishmentType banType;
        if (getExpiresAt() == -1) {
            banMessage = Messages.BAN_PERMANENT_ALERT + "";
            banType = PunishmentType.PERM_BAN;
        } else {
            banMessage = Messages.BAN_TEMPORARY_ALERT + "";
            banType = PunishmentType.TEMP_BAN;
        }

        PluginMessageUtil.sendData("BungeeCord", "KickPlayer", getTargetName(), Placeholders.parsePlaceholder(
                banMessage,
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
                banType,
                getReason(),
                getDurationText(),
                isSilent()
        );
    }

}
