package me.blurmit.basics.command.defined.punishment;

import me.blurmit.basics.Basics;
import me.blurmit.basics.punishments.PunishmentType;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class UnbanCommand extends PunishmentCommand {

    private final Basics plugin;

    public UnbanCommand(Basics plugin) {
        super(plugin);
        setName("unban");
        setDescription("Unbans a player");
        setUsage("/unban [-s] <player> <reason>");
        setPermission("basics.command.unban");

        this.plugin = plugin;
    }

    @Override
    public void punish(CommandSender moderator, UUID target, String command, String[] args, boolean isSilent, long expiresAt) {
        if (!plugin.getPunishmentManager().isBanned(target)) {
            moderator.sendMessage(Placeholders.parsePlaceholder(Messages.NOT_BANNED + "", true, args[0]));
            return;
        }

        plugin.getPunishmentManager().getBannedPlayers().remove(getTargetUUID());

        moderator.sendMessage(Placeholders.parsePlaceholder(
                Messages.PUNISHMENT_MESSAGE + "", true, "unbanned", getFancyTargetName(), getReason()
        ));

        plugin.getPunishmentManager().storeUnban(getTargetUUID(), getModUUID(), getReason());
        plugin.getPunishmentManager().broadcastPardon(moderator, getFancyTargetName(), PunishmentType.UNBAN, getReason(), isSilent());
    }

}
