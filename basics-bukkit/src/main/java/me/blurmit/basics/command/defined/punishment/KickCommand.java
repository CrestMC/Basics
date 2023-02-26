package me.blurmit.basics.command.defined.punishment;

import me.blurmit.basics.Basics;
import me.blurmit.basics.punishments.PunishmentType;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.PluginMessageUtil;
import me.blurmit.basics.util.TimeUtil;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class KickCommand extends PunishmentCommand {

    private final Basics plugin;

    public KickCommand(Basics plugin) {
        super(plugin);
        setName("kick");
        setDescription("Kicks a player from the server");
        setUsage("/kick [-s] <player> <reason>");
        setPermission("basics.command.kick");

        this.plugin = plugin;
    }

    @Override
    public void punish(CommandSender moderator, UUID target, String command, String[] args, boolean isSilent, long expiresAt) {
        PluginMessageUtil.sendData("BungeeCord", "KickPlayer", getTargetName(), Placeholders.parsePlaceholder(
                Messages.KICK_ALERT + "", true, getReason(), getExpiresInText()
        ));

        moderator.sendMessage(Placeholders.parsePlaceholder(
                Messages.PUNISHMENT_MESSAGE + "", true, "kicked", getTargetName(), getReason(), getDurationText()
        ));

        plugin.getPunishmentManager().storeHistory(PunishmentType.KICK, getTargetUUID(), getModUUID(), getReason(), TimeUtil.getCurrentTimeSeconds(), getExpiresAt());
        plugin.getPunishmentManager().broadcastPunishment(moderator, getFancyTargetName(), PunishmentType.KICK, getReason(), getDurationText(), isSilent());
    }

}
