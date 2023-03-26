package me.blurmit.basics.command.defined.punishment;

import me.blurmit.basics.Basics;
import me.blurmit.basics.punishments.PunishmentType;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class UnmuteCommand extends PunishmentCommand {

    private final Basics plugin;

    public UnmuteCommand(Basics plugin) {
        super(plugin);
        setName("unmute");
        setDescription("Unmutes a player");
        setUsage("/unmute [-s] <player> <reason>");
        setPermission("basics.command.unmute");

        this.plugin = plugin;
    }

    @Override
    public void punish(CommandSender moderator, UUID target, String command, String[] args, boolean isSilent, long expiresAt) {
        if (!plugin.getPunishmentManager().isMuted(target)) {
            moderator.sendMessage(Placeholders.parse(Messages.NOT_MUTED + "", true, args[0]));
            return;
        }

        plugin.getPunishmentManager().getMutedPlayers().remove(getTargetUUID());

        moderator.sendMessage(Placeholders.parse(
                Messages.PUNISHMENT_MESSAGE + "", true, "unmuted", getFancyTargetName(), getReason()
        ));

        plugin.getPunishmentManager().storeUnmute(getTargetUUID(), getModUUID(), getReason());
        plugin.getPunishmentManager().broadcastPardon(moderator, getFancyTargetName(), PunishmentType.UNMUTE, getReason(), isSilent());
    }

}
