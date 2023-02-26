package me.blurmit.basics.punishments;

import javafx.util.Pair;
import lombok.Getter;
import me.blurmit.basics.Basics;
import me.blurmit.basics.punishments.data.PunishmentData;
import me.blurmit.basics.punishments.storage.PunishmentStorageManager;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.RankUtil;
import me.blurmit.basics.util.TimeUtil;
import me.blurmit.basics.util.lang.Messages;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class PunishmentManager {

    private final Basics plugin;
    private final String serverName;
    @Getter
    private final PunishmentStorageManager storage;

    @Getter
    private final Map<UUID, BukkitTask> frozenPlayers;
    @Getter
    private final Map<UUID, BukkitTask> mutedPlayers;
    @Getter
    private final Map<UUID, BukkitTask> bannedPlayers;

    public PunishmentManager(Basics plugin) {
        this.plugin = plugin;
        this.storage = new PunishmentStorageManager(plugin);
        this.serverName = plugin.getConfigManager().getConfig().getString("Server-Name.Default-Value");

        this.frozenPlayers = new HashMap<>();
        this.mutedPlayers = new HashMap<>();
        this.bannedPlayers = new HashMap<>();

        new PunishmentsListener(plugin);
    }

    public void storeBan(UUID target, UUID moderator, long expiresAt, String reason) {
        long punishedAt = TimeUtil.getCurrentTimeSeconds();

        storage.getStorageProvider().storeBan(target, moderator, reason, punishedAt, expiresAt, serverName);
        storage.getStorageProvider().storePunishment(PunishmentType.BAN, target, moderator, reason, punishedAt, expiresAt, serverName);
    }

    public void storeUnban(UUID target, UUID moderator, String reason) {
        long punishedAt = TimeUtil.getCurrentTimeSeconds();

        storage.getStorageProvider().storeUnban(target, moderator, reason, serverName);
        storage.getStorageProvider().storePunishment(PunishmentType.UNBAN, target, moderator, reason, punishedAt, punishedAt, serverName);

        if (getBannedPlayers().containsKey(target)) {
            getBannedPlayers().get(target).cancel();
            getBannedPlayers().remove(target);
        }
    }

    public void storeBlacklist(UUID target, UUID moderator, long expiresAt, String reason) {
        long punishedAt = TimeUtil.getCurrentTimeSeconds();

        storage.getStorageProvider().storeBlacklist(target, moderator, reason, punishedAt, expiresAt, serverName);
        storage.getStorageProvider().storePunishment(PunishmentType.BLACKLIST, target, moderator, reason, punishedAt, expiresAt, serverName);
    }

    public void storeUnblacklist(UUID target, UUID moderator, String reason) {
        long punishedAt = TimeUtil.getCurrentTimeSeconds();

        storage.getStorageProvider().storeUnblacklist(target, moderator, reason, serverName);
        storage.getStorageProvider().storePunishment(PunishmentType.UNBLACKLIST, target, moderator, reason, punishedAt, punishedAt, serverName);

        if (getBannedPlayers().containsKey(target)) {
            getBannedPlayers().get(target).cancel();
            getBannedPlayers().remove(target);
        }
    }

    public void storeMute(UUID target, UUID moderator, long expiresAt, String reason) {
        long punishedAt = TimeUtil.getCurrentTimeSeconds();

        storage.getStorageProvider().storeMute(target, moderator, reason, punishedAt, expiresAt, serverName);
        storage.getStorageProvider().storePunishment(PunishmentType.MUTE, target, moderator, reason, punishedAt, expiresAt, serverName);
    }

    public void storeUnmute(UUID target, UUID moderator, String reason) {
        long punishedAt = TimeUtil.getCurrentTimeSeconds();

        storage.getStorageProvider().storeUnmute(target, moderator, reason, serverName);
        storage.getStorageProvider().storePunishment(PunishmentType.UNMUTE, target, moderator, reason, punishedAt, punishedAt, serverName);

        if (getMutedPlayers().containsKey(target)) {
            getMutedPlayers().get(target).cancel();
            getMutedPlayers().remove(target);
        }
    }

    public void storeHistory(PunishmentType type, UUID target, UUID moderator, String reason, long punishedAt, long expiresAt) {
        storage.getStorageProvider().storePunishment(type, target, moderator, reason, punishedAt, expiresAt, serverName);
    }

    public String getBanReason(UUID target) {
        return getPunishmentReason(PunishmentType.BAN, target);
    }

    public String getBlacklistReason(UUID target) {
        return getPunishmentReason(PunishmentType.BLACKLIST, target);
    }

    public String getMuteReason(UUID target) {
        return getPunishmentReason(PunishmentType.MUTE, target);
    }

    public String getPunishmentReason(PunishmentType type, UUID target) {
        return getPunishmentData(type, target).getReason();
    }

    public long getBlacklistDuration(UUID target) {
        return getPunishmentDuration(PunishmentType.BLACKLIST, target);
    }

    public long getBanDuration(UUID target) {
        return getPunishmentDuration(PunishmentType.BAN, target);
    }

    public long getMuteDuration(UUID target) {
        return getPunishmentDuration(PunishmentType.MUTE, target);
    }

    public long getPunishmentDuration(PunishmentType type, UUID target) {
        return getPunishmentData(type, target).getExpiresAt();
    }

    public boolean isBlacklisted(UUID target) {
        return isPunishmentActive(PunishmentType.BLACKLIST, target);
    }

    public boolean isBanned(UUID target) {
        return isPunishmentActive(PunishmentType.BAN, target);
    }

    public boolean isMuted(UUID target) {
        return isPunishmentActive(PunishmentType.MUTE, target);
    }

    public boolean isPunishmentActive(PunishmentType type, UUID target) {
        return getPunishmentData(type, target) != null;
    }

    public PunishmentData getPunishmentData(PunishmentType type, UUID target) {
        try {
            return storage.getStorageProvider().getPunishmentData(type, target).get();
        } catch (InterruptedException | ExecutionException | CancellationException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to retrieve punishment data of " + target, e);
            return null;
        }
    }

    public Pair<Boolean, String[]> isSilent(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-s")) {
                args = Arrays.stream(args).filter(argument -> !argument.equals("-s")).toArray(String[]::new);
                return new Pair<>(true, args);
            }
        }

        return new Pair<>(false, args);
    }

    public void broadcastPunishment(CommandSender sender, String targetName, PunishmentType punishment, String reason, boolean silent) {
        broadcastPunishment(sender, targetName, punishment, reason, "forever", silent);
    }

    public void broadcastPunishment(CommandSender sender, String targetName, PunishmentType punishment, String reason, String duration, boolean silent) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!player.hasPermission(silent ? "basics.punishments.viewsilent" : "basics.player")) {
                continue;
            }

            String senderName = RankUtil.getColoredName(sender);

            TextComponent message = new TextComponent(Placeholders.parsePlaceholder(
                    (silent ? Messages.PUNISHMENT_SILENT_PREFIX.toString() : "") + punishment.message(),
                    true,
                    targetName,
                    senderName,
                    duration));

            if (player.hasPermission("basics.punishments.viewdetails")) {
                HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Placeholders.parsePlaceholder(
                        Messages.PUNISHMENT_HOVER.toString(),
                        true,
                        senderName,
                        reason,
                        duration
                )).create());
                message.setHoverEvent(hover);
            }

            player.spigot().sendMessage(message);
        }
    }

    public void broadcastPardon(CommandSender sender, String targetName, PunishmentType punishment, String reason, boolean silent) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!player.hasPermission(silent ? "basics.punishments.viewsilent" : "basics.player")) {
                continue;
            }

            String senderName = RankUtil.getColoredName(sender);

            TextComponent message = new TextComponent(Placeholders.parsePlaceholder(
                    (silent ? Messages.PUNISHMENT_SILENT_PREFIX.toString() : "") + punishment.message(),
                    true,
                    targetName,
                    punishment.equals(PunishmentType.UNBAN) ? "unbanned" : punishment.equals(PunishmentType.UNBLACKLIST) ? "unblacklisted" : "unmuted",
                    senderName));

            if (player.hasPermission("basics.punishments.viewdetails")) {
                HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Placeholders.parsePlaceholder(
                        Messages.PUNISHMENT_HOVER.toString(),
                        true,
                        senderName,
                        reason
                )).create());
                message.setHoverEvent(hover);
            }

            player.spigot().sendMessage(message);
        }
    }

}
