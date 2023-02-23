package me.blurmit.basics.command.defined.punishment;

import javafx.util.Pair;
import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.punishments.PunishmentType;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.PluginMessageUtil;
import me.blurmit.basics.util.RankUtil;
import me.blurmit.basics.util.TimeUtil;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
        moderator.sendMessage(Placeholders.parsePlaceholder(
                Messages.PUNISHMENT_MESSAGE + "",
                true,
                "warned",
                getTargetName(),
                getReason(),
                getDurationText()
        ));

        Player targetPlayer = plugin.getServer().getPlayer(target);
        if (targetPlayer != null) {
            targetPlayer.sendMessage(Placeholders.parsePlaceholder(
                    Messages.WARN_ALERT + "",
                    true,
                    getReason(),
                    getExpiresInText()
            ));
            return;
        }

        plugin.getPunishmentManager().storeHistory(
                PunishmentType.WARN,
                getTargetUUID(),
                getModUUID(),
                TimeUtil.getCurrentTimeSeconds(),
                getExpiresAt(),
                getServerName(),
                getReason()
        );
        plugin.getPunishmentManager().broadcastPunishment(
                moderator,
                getFancyTargetName(),
                PunishmentType.WARN,
                getReason(),
                getDurationText(),
                isSilent()
        );
    }

}
