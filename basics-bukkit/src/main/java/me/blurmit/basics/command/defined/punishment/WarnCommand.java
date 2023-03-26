package me.blurmit.basics.command.defined.punishment;

import me.blurmit.basics.Basics;
import me.blurmit.basics.punishments.PunishmentType;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.TimeUtil;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WarnCommand extends PunishmentCommand {

    private final Basics plugin;

    public WarnCommand(Basics plugin) {
        super(plugin);
        setName("warn");
        setDescription("Warns a player on the server");
        setUsage("/warn [-s] <player> <reason>");
        setPermission("basics.command.warn");

        this.plugin = plugin;
    }

    @Override
    public void punish(CommandSender moderator, UUID target, String command, String[] args, boolean isSilent, long expiresAt) {
        moderator.sendMessage(Placeholders.parse(
                Messages.PUNISHMENT_MESSAGE + "", true, "warned", getTargetName(), getReason(), getDurationText()
        ));

        Player player = plugin.getServer().getPlayer(target);
        if (player != null) {
            player.sendMessage(Placeholders.parse(Messages.WARN_ALERT + "", true, getReason(), getExpiresInText()));
        }

        plugin.getPunishmentManager().storeHistory(PunishmentType.WARN, getTargetUUID(), getModUUID(), getReason(), TimeUtil.getCurrentTimeSeconds(), getExpiresAt());
        plugin.getPunishmentManager().broadcastPunishment(moderator, getFancyTargetName(), PunishmentType.WARN, getReason(), getDurationText(), isSilent());
    }

}
