package me.blurmit.basics.command.defined.punishment;

import javafx.util.Pair;
import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.RankUtil;
import me.blurmit.basics.util.TimeUtil;
import me.blurmit.basics.util.UUIDUtil;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;

public abstract class PunishmentCommand extends CommandBase {

    final Basics plugin;

    private UUID moderatorUUID;

    private String fancyTargetName;
    private String targetName;
    private UUID targetUUID;

    private final String serverName;
    private String reason;
    private boolean isSilent;
    private long expiresAt;
    private String expiresInText;
    private String durationText;
    private String[] args;

    public PunishmentCommand(Basics plugin) {
        super(plugin.getName());
        this.plugin = plugin;
        this.moderatorUUID = null;
        this.targetUUID = null;
        this.isSilent = false;
        this.expiresAt = -1;
        this.fancyTargetName = "invalid-Player";
        this.targetName = "invalid-Player";
        this.durationText = "forever";
        this.expiresInText = "never";
        this.args = new String[0];
        this.serverName = plugin.getConfigManager().getConfig().getString("Server-Name.Default-Value");
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] arguments) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!sender.hasPermission(getPermission())) {
                sender.sendMessage(Placeholders.parse(Messages.NO_PERMISSION + "", sender, this, arguments, true));
                return;
            }

            // Determined if the punishment will be silent
            Pair<Boolean, String[]> silentPair = plugin.getPunishmentManager().isSilent(arguments);
            isSilent = silentPair.getKey();
            args = silentPair.getValue();

            // Calculate the punishment length, and reparse the arguments
            Pair<Long, String[]> durationPair = TimeUtil.getExpireTime(args);
            if (durationPair == null) {
                expiresAt = -1;
            } else {
                expiresAt = durationPair.getKey();
                args = durationPair.getValue();
            }

            // Check if the arguments are greater than 2, after they've been parsed
            if (args.length < 2) {
                sender.sendMessage(Placeholders.parse(Messages.INVALID_ARGS + "", sender, this, args, true));
                return;
            }

            // Check if the sender is console or not. If it's a player, change the UUID, else, leave it as null.
            if (sender instanceof Player) {
                moderatorUUID = ((Player) sender).getUniqueId();
            }

            // Get the target's UUID, and formatted name
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target != null) {
                targetUUID = target.getUniqueId();
            } else {
                targetUUID = UUIDUtil.getUUID(args[0]);

                if (targetUUID == null) {
                    sender.sendMessage(Placeholders.parse(Messages.ACCOUNT_DOESNT_EXIST + "", true, args[0]));
                    return;
                }
            }
            Pair<String, String> namePair = RankUtil.getColoredNamePair(targetUUID);
            fancyTargetName = namePair.getKey();
            targetName = namePair.getValue();

            // After all arguments have been parsed, use the remaining arguments as a reason
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            expiresInText = TimeUtil.getHowLongUntil(expiresAt);
            durationText = TimeUtil.getDurationFrom(arguments);

            punish(sender, targetUUID, commandLabel, args, isSilent, expiresAt);
        });

        return true;
    }

    public abstract void punish(CommandSender moderator, UUID target, String command, String[] args, boolean isSilent, long expiresAt);

    public UUID getModUUID() {
        return moderatorUUID;
    }

    public UUID getTargetUUID() {
        return targetUUID;
    }

    public String getFancyTargetName() {
        return fancyTargetName;
    }
    public String getTargetName() {
        return targetName;
    }

    public boolean isSilent() {
        return isSilent;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public String getExpiresInText() {
        return expiresInText;
    }

    public String getDurationText() {
        return durationText;
    }

    public String getServerName() {
        return serverName;
    }

    public String getReason() {
        return reason;
    }

}
