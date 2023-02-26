package me.blurmit.basics.command.defined.punishment;

import me.blurmit.basics.Basics;
import me.blurmit.basics.punishments.PunishmentType;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.TimeUtil;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class MuteCommand extends PunishmentCommand {

    private final Basics plugin;

    public MuteCommand(Basics plugin) {
        super(plugin);
        setName("mute");
        setDescription("Prevents a player from speaking in chat");
        setUsage("/mute [-s] <player> <reason>");
        setPermission("basics.command.mute");

        this.plugin = plugin;
    }

    @Override
    public void punish(CommandSender moderator, UUID target, String command, String[] args, boolean isSilent, long expiresAt) {
        String muteMessage = Messages.MUTE_PERMANENT_ALERT + "";
        PunishmentType muteType = PunishmentType.PERM_MUTE;

        if (getExpiresAt() != -1) {
            muteMessage = Messages.MUTE_TEMPORARY_ALERT + "";
            muteType = PunishmentType.TEMP_MUTE;
        }

        moderator.sendMessage(Placeholders.parsePlaceholder(
                Messages.PUNISHMENT_MESSAGE + "", true, "muted", getTargetName(), getReason(), getDurationText()
        ));

        Player targetPlayer = plugin.getServer().getPlayer(target);
        if (targetPlayer != null) {
            targetPlayer.sendMessage(Placeholders.parsePlaceholder(muteMessage, true, getReason(), getExpiresInText()));

            long timeLeft = expiresAt - TimeUtil.getCurrentTimeSeconds();
            BukkitTask task = null;

            if (getExpiresAt() != -1) {
                task = plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> plugin.getPunishmentManager().storeUnmute(target, null, "Expired"), timeLeft * 20L);
            }

            plugin.getPunishmentManager().getMutedPlayers().put(target, task);
        }

        plugin.getPunishmentManager().storeMute(getTargetUUID(), getModUUID(), getExpiresAt(), getReason());
        plugin.getPunishmentManager().broadcastPunishment(moderator, getFancyTargetName(), muteType, getReason(), getDurationText(), isSilent());
    }

}
