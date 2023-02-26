package me.blurmit.basics.command.defined.punishment;

import me.blurmit.basics.Basics;
import me.blurmit.basics.punishments.PunishmentType;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.UUID;

public class UnblacklistCommand extends PunishmentCommand {

    private final Basics plugin;

    public UnblacklistCommand(Basics plugin) {
        super(plugin);
        setName("unblacklist");
        setDescription("Unblacklists a player");
        setUsage("/unblacklist [-s] <player> <reason>");
        setPermission("basics.command.unblacklist");
        setAliases(Arrays.asList("unipban", "pardonip", "unipban", "removeipban"));

        this.plugin = plugin;
    }

    @Override
    public void punish(CommandSender moderator, UUID target, String command, String[] args, boolean isSilent, long expiresAt) {
        if (!plugin.getPunishmentManager().isBlacklisted(target)) {
            moderator.sendMessage(Placeholders.parsePlaceholder(Messages.NOT_BLACKLISTED + "", true, args[0]));
            return;
        }

        plugin.getPunishmentManager().getBannedPlayers().remove(getTargetUUID());

        moderator.sendMessage(Placeholders.parsePlaceholder(
                Messages.PUNISHMENT_MESSAGE + "", true, "unblacklisted", getFancyTargetName(), getReason()
        ));

        plugin.getPunishmentManager().storeUnblacklist(getTargetUUID(), getModUUID(), getReason());
        plugin.getPunishmentManager().broadcastPardon(moderator, getFancyTargetName(), PunishmentType.UNBLACKLIST, getReason(), isSilent());
    }

}
