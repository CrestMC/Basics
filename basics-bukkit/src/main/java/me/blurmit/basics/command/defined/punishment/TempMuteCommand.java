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

public class TempMuteCommand extends PunishmentCommand {

    private final Basics plugin;

    public TempMuteCommand(Basics plugin) {
        super(plugin);
        setName("tempmute");
        setDescription("Temporarily prevents a player from speaking in chat");
        setUsage("/tempmute [-s] <player> <reason>");
        setPermission("basics.command.tempmute");

        this.plugin = plugin;
    }

    @Override
    public void punish(CommandSender moderator, UUID target, String command, String[] args, boolean isSilent, long expiresAt) {
        if (getExpiresAt() == -1) {
            moderator.sendMessage(Placeholders.parsePlaceholder(Messages.PUNISHMENT_NO_TIME_PROVIDED + ""));
            return;
        }

        moderator.sendMessage(Placeholders.parsePlaceholder(
                Messages.PUNISHMENT_MESSAGE + "",
                true,
                "muted",
                getTargetName(),
                getReason(),
                getDurationText()
        ));

        Player targetPlayer = plugin.getServer().getPlayer(target);
        if (targetPlayer != null) {
            targetPlayer.sendMessage(Placeholders.parsePlaceholder(
                    Messages.MUTE_TEMPORARY_ALERT + "",
                    true,
                    getReason(),
                    getExpiresInText()
            ));
            plugin.getPunishmentManager().getMutedPlayers().add(target);
        }

        plugin.getPunishmentManager().storeMute(
                getTargetUUID(),
                getModUUID(),
                getExpiresAt(),
                getServerName(),
                getReason()
        );
        plugin.getPunishmentManager().broadcastPunishment(
                moderator,
                getFancyTargetName(),
                PunishmentType.TEMP_MUTE,
                getReason(),
                getDurationText(),
                isSilent()
        );
    }

}
