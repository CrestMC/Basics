package me.blurmit.basics.punishments;

import javafx.util.Pair;
import lombok.Getter;
import me.blurmit.basics.Basics;
import me.blurmit.basics.database.StorageType;
import me.blurmit.basics.punishments.storage.PunishmentStorage;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.RankUtil;
import me.blurmit.basics.util.TimeUtil;
import me.blurmit.basics.util.lang.Messages;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class PunishmentManager {

    private final Basics plugin;
    private final PunishmentsListener listener;

    @Getter
    private final PunishmentStorage storage;

    @Getter
    private final Set<UUID> frozenPlayers;
    @Getter
    private final Set<UUID> mutedPlayers;
    @Getter
    private final Set<UUID> bannedPlayers;

    public PunishmentManager(Basics plugin) {
        this.plugin = plugin;
        this.storage = new PunishmentStorage(plugin);
        this.listener = new PunishmentsListener(plugin);

        this.frozenPlayers = new HashSet<>();
        this.mutedPlayers = new HashSet<>();
        this.bannedPlayers = new HashSet<>();
    }

    public void storeBan(UUID target, UUID moderator, long until, String server, String reason) {
        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_bans` WHERE `uuid` = ?");
                queryStatement.setString(1, target.toString());

                // Delete current ban from database if it exists.
                ResultSet result = queryStatement.executeQuery();
                if (result.next()) {
                    storeUnban(target, moderator, "Ban Override");
                }

                PreparedStatement banStatement = connection.prepareStatement("INSERT IGNORE INTO `basics_bans` (`uuid`, `moderator_uuid`, `punished_at`, `expires_at`, `server`, `reason`) VALUES (?, ?, ?, ?, ?, ?)");
                banStatement.setString(1, target.toString());
                banStatement.setString(2, moderator == null ? "null" : moderator.toString());
                banStatement.setLong(3, TimeUtil.getCurrentTimeSeconds());
                banStatement.setLong(4, until);
                banStatement.setString(5, server);
                banStatement.setString(6, reason);
                banStatement.execute();
            });
        } else {
            // Delete current ban from database if it exists.
            if (plugin.getConfigManager().getPunishmentsConfig().getConfigurationSection("bans." + target) != null) {
                storeUnban(target, moderator, "Ban Override");
            }

            plugin.getConfigManager().getPunishmentsConfig().set("bans." + target + ".moderator-uuid", moderator == null ? "null" : moderator.toString());
            plugin.getConfigManager().getPunishmentsConfig().set("bans." + target + ".punished_at", TimeUtil.getCurrentTimeSeconds());
            plugin.getConfigManager().getPunishmentsConfig().set("bans." + target + ".expires_at", until);
            plugin.getConfigManager().getPunishmentsConfig().set("bans." + target + ".reason", reason);
            plugin.getConfigManager().getPunishmentsConfig().set("bans." + target + ".server", server);
            plugin.getConfigManager().savePunishmentsConfig();
        }

        storeHistory(PunishmentType.BAN, target, moderator, TimeUtil.getCurrentTimeSeconds(), until, server, reason);
    }

    public void storeUnban(UUID uuid, UUID moderator_uuid, String reason) {
        String server = plugin.getConfigManager().getConfig().getString("Server-Name.Default-Value");

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_bans` WHERE `uuid` = ?");
                queryStatement.setString(1, uuid.toString());

                ResultSet results = queryStatement.executeQuery();
                while (results.next()) {
                    storeHistory(PunishmentType.UNBAN, uuid, moderator_uuid, results.getLong("punished_at"), TimeUtil.getCurrentTimeSeconds(), server, reason);
                }

                PreparedStatement unbanStatement = connection.prepareStatement("DELETE FROM `basics_bans` WHERE `uuid` = ?");
                unbanStatement.setString(1, uuid.toString());
                unbanStatement.execute();
            });
        } else {
            long punishedAt = plugin.getConfigManager().getPunishmentsConfig().getLong("bans." + uuid + ".punished_at");
            storeHistory(PunishmentType.UNBAN, uuid, moderator_uuid, punishedAt, TimeUtil.getCurrentTimeSeconds(), server, reason);

            if (plugin.getConfigManager().getPunishmentsConfig().getConfigurationSection("bans." + uuid) != null) {
                plugin.getConfigManager().getPunishmentsConfig().set("bans." + uuid, null);
                plugin.getConfigManager().savePunishmentsConfig();
            }
        }
    }

    public void storeBlacklist(UUID player, UUID moderator_uuid, long until, String server, String reason, String ip) {
        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_blacklists` WHERE `uuid` = ?");
                queryStatement.setString(1, player.toString());

                // Delete current blacklist from database if it exists.
                ResultSet result = queryStatement.executeQuery();
                if (result.next()) {
                    storeUnblacklist(ip, moderator_uuid, "Blacklist Override");
                }

                PreparedStatement blacklistStatement = connection.prepareStatement("INSERT IGNORE INTO `basics_blacklists` (`uuid`, `ip`, `moderator_uuid`, `punished_at`, `expires_at`, `server`, `reason`) VALUES (?, ?, ?, ?, ?, ?, ?)");
                blacklistStatement.setString(1, player.toString());
                blacklistStatement.setString(2, ip);
                blacklistStatement.setString(3, moderator_uuid == null ? "null" : moderator_uuid.toString());
                blacklistStatement.setLong(4, TimeUtil.getCurrentTimeSeconds());
                blacklistStatement.setLong(5, until);
                blacklistStatement.setString(6, server);
                blacklistStatement.setString(7, reason);
                blacklistStatement.execute();
            });
        } else {
            // Delete current blacklist from database if it exists.
            if (plugin.getConfigManager().getPunishmentsConfig().getConfigurationSection("blacklists." + player) != null) {
                storeUnblacklist(ip, moderator_uuid, "Ban Override");
            }

            plugin.getConfigManager().getPunishmentsConfig().set("blacklists." + player + ".ip", ip);
            plugin.getConfigManager().getPunishmentsConfig().set("blacklists." + player + ".moderator-uuid", moderator_uuid == null ? "null" : moderator_uuid.toString());
            plugin.getConfigManager().getPunishmentsConfig().set("blacklists." + player + ".punished_at", TimeUtil.getCurrentTimeSeconds());
            plugin.getConfigManager().getPunishmentsConfig().set("blacklists." + player + ".expires_at", until);
            plugin.getConfigManager().getPunishmentsConfig().set("blacklists." + player + ".reason", reason);
            plugin.getConfigManager().getPunishmentsConfig().set("blacklists." + player + ".server", server);
            plugin.getConfigManager().savePunishmentsConfig();
        }

        storeHistory(PunishmentType.BLACKLIST, player, moderator_uuid, TimeUtil.getCurrentTimeSeconds(), until, server, reason);
    }

    public void storeUnblacklist(String ip, UUID moderator_uuid, String reason) {
        String server = plugin.getConfigManager().getConfig().getString("Server-Name.Default-Value");

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_blacklists` WHERE `ip` = ?");
                queryStatement.setString(1, ip);

                ResultSet results = queryStatement.executeQuery();
                while (results.next()) {
                    storeHistory(PunishmentType.UNBLACKLIST, UUID.fromString(results.getString("uuid")), moderator_uuid, results.getLong("punished_at"), TimeUtil.getCurrentTimeSeconds(), server, reason);
                }

                PreparedStatement unbanStatement = connection.prepareStatement("DELETE FROM `basics_blacklists` WHERE `ip` = ?");
                unbanStatement.setString(1, ip);
                unbanStatement.execute();
            });
        } else {
            ConfigurationSection section = plugin.getConfigManager().getPunishmentsConfig().getConfigurationSection("blacklists");
            section.getValues(false).forEach((player, punishment_data) -> {
                ConfigurationSection data = (ConfigurationSection) punishment_data;

                if (data.getString("ip").equals(ip)) {
                    plugin.getConfigManager().getPunishmentsConfig().set("blacklists." + player, null);
                    plugin.getConfigManager().savePunishmentsConfig();

                    long punishedAt = plugin.getConfigManager().getPunishmentsConfig().getLong("blacklists." + player + ".punished_at");
                    storeHistory(PunishmentType.UNBLACKLIST, UUID.fromString(player), moderator_uuid, punishedAt, TimeUtil.getCurrentTimeSeconds(), server, reason);
                }
            });
        }
    }

    public void storeMute(UUID player, UUID moderator_uuid, long until, String server, String reason) {
        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_mutes` WHERE `uuid` = ?");
                queryStatement.setString(1, player.toString());

                // Delete current mute from database if it exists.
                ResultSet result = queryStatement.executeQuery();
                if (result.next()) {
                    storeUnmute(player, moderator_uuid, "Mute Override");
                }

                PreparedStatement muteStatement = connection.prepareStatement("INSERT IGNORE INTO `basics_mutes` (`uuid`, `moderator_uuid`, `punished_at`, `expires_at`, `server`, `reason`) VALUES (?, ?, ?, ?, ?, ?)");
                muteStatement.setString(1, player.toString());
                muteStatement.setString(2, moderator_uuid == null ? "null" : moderator_uuid.toString());
                muteStatement.setLong(3, TimeUtil.getCurrentTimeSeconds());
                muteStatement.setLong(4, until);
                muteStatement.setString(5, server);
                muteStatement.setString(6, reason);
                muteStatement.execute();
            });
        } else {
            // Delete current mute from database if it exists.
            if (plugin.getConfigManager().getPunishmentsConfig().getConfigurationSection("mutes." + player) != null) {
                storeUnmute(player, moderator_uuid, "Mute Override");
            }

            plugin.getConfigManager().getPunishmentsConfig().set("mutes." + player + ".moderator-uuid", moderator_uuid == null ? "null" : moderator_uuid.toString());
            plugin.getConfigManager().getPunishmentsConfig().set("mutes." + player + ".punished_at", TimeUtil.getCurrentTimeSeconds());
            plugin.getConfigManager().getPunishmentsConfig().set("mutes." + player + ".expires_at", until);
            plugin.getConfigManager().getPunishmentsConfig().set("mutes." + player + ".reason", reason);
            plugin.getConfigManager().getPunishmentsConfig().set("mutes." + player + ".server", server);
            plugin.getConfigManager().savePunishmentsConfig();
        }

        storeHistory(PunishmentType.MUTE, player, moderator_uuid, TimeUtil.getCurrentTimeSeconds(), until, server, reason);
    }

    public void storeUnmute(UUID uuid, UUID moderator_uuid, String reason) {
        String server = plugin.getConfigManager().getConfig().getString("Server-Name.Default-Value");

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_mutes` WHERE `uuid` = ?");
                queryStatement.setString(1, uuid.toString());

                ResultSet results = queryStatement.executeQuery();
                while (results.next()) {
                    storeHistory(PunishmentType.UNMUTE, uuid, moderator_uuid, results.getLong("punished_at"), TimeUtil.getCurrentTimeSeconds(), server, reason);
                }

                PreparedStatement unbanStatement = connection.prepareStatement("DELETE FROM `basics_mutes` WHERE `uuid` = ?");
                unbanStatement.setString(1, uuid.toString());
                unbanStatement.execute();
            });
        } else {
            long punishedAt = plugin.getConfigManager().getPunishmentsConfig().getLong("mutes." + uuid + ".punished_at");
            storeHistory(PunishmentType.UNMUTE, uuid, moderator_uuid, punishedAt, TimeUtil.getCurrentTimeSeconds(), server, reason);

            if (plugin.getConfigManager().getPunishmentsConfig().getConfigurationSection("mutes." + uuid) != null) {
                plugin.getConfigManager().getPunishmentsConfig().set("mutes." + uuid, null);
                plugin.getConfigManager().savePunishmentsConfig();
            }
        }
    }

    public void storeHistory(PunishmentType punishment, UUID player, UUID moderator_uuid, long punished_at, long until, String server, String reason) {
        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement historyStatement = connection.prepareStatement("INSERT IGNORE INTO `basics_history` (`punishment`, `uuid`, `moderator_uuid`, `punished_at`, `expires_at`, `server`, `reason`) VALUES (?, ?, ?, ?, ?, ?, ?)");
                historyStatement.setString(1, punishment.name());
                historyStatement.setString(2, player.toString());
                historyStatement.setString(3, moderator_uuid == null ? "null" : moderator_uuid.toString());
                historyStatement.setLong(4, punished_at);
                historyStatement.setLong(5, until);
                historyStatement.setString(6, server);
                historyStatement.setString(7, reason);
                historyStatement.execute();
            });
        } else {
            ConfigurationSection section = plugin.getConfigManager().getPunishmentsConfig().getConfigurationSection("history." + player);
            int id = section != null ? section.getValues(false).size() + 1 : 0;

            plugin.getConfigManager().getPunishmentsConfig().set("history." + player + "." + id + ".punishment", punishment.name());
            plugin.getConfigManager().getPunishmentsConfig().set("history." + player + "." + id + ".moderator_uuid", moderator_uuid == null ? "null" : moderator_uuid.toString());
            plugin.getConfigManager().getPunishmentsConfig().set("history." + player + "." + id + ".punished_at", punished_at);
            plugin.getConfigManager().getPunishmentsConfig().set("history." + player + "." + id + ".expires_at", until);
            plugin.getConfigManager().getPunishmentsConfig().set("history." + player + "." + id + ".server", server);
            plugin.getConfigManager().getPunishmentsConfig().set("history." + player + "." + id + ".reason", reason);
            plugin.getConfigManager().savePunishmentsConfig();
        }
    }

    public String getBanReason(UUID player) {
        AtomicReference<String> reason = new AtomicReference<>();

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM `basics_bans` WHERE `uuid` = ?");
                statement.setString(1, player.toString());

                ResultSet results = statement.executeQuery();
                while (results.next()) {
                    reason.set(results.getString("reason"));
                }
            });
        } else {
            reason.set(plugin.getConfigManager().getPunishmentsConfig().getString("bans." + player + ".reason"));
        }

        return reason.get();
    }

    public String getBlacklistReason(UUID player) {
        AtomicReference<String> reason = new AtomicReference<>();

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM `basics_blacklists` WHERE `uuid` = ?");
                statement.setString(1, player.toString());

                ResultSet results = statement.executeQuery();
                while (results.next()) {
                    reason.set(results.getString("reason"));
                }
            });
        } else {
            reason.set(plugin.getConfigManager().getPunishmentsConfig().getString("blacklists." + player + ".reason"));
        }

        return reason.get();
    }

    public String getBlacklistReason(String ip) {
        AtomicReference<String> reason = new AtomicReference<>();

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM `basics_blacklists` WHERE `ip` = ?");
                statement.setString(1, ip);

                ResultSet results = statement.executeQuery();
                while (results.next()) {
                    reason.set(results.getString("reason"));
                }
            });
        } else {
            ConfigurationSection section = plugin.getConfigManager().getPunishmentsConfig().getConfigurationSection("blacklists");

            section.getValues(false).forEach((player, data) -> {
                ConfigurationSection punishment_data = (ConfigurationSection) data;

                if (punishment_data.getString("ip").equals(ip)) {
                    long expiresAt = section.getLong("expires_at");

                    if (expiresAt > TimeUtil.getCurrentTimeSeconds() && expiresAt != -1) {
                        plugin.getConfigManager().getPunishmentsConfig().set("blacklists." + player, null);
                        plugin.getConfigManager().savePunishmentsConfig();
                        return;
                    }

                    reason.set(punishment_data.getString("reason"));
                }
            });
        }

        return reason.get();
    }

    public String getMuteReason(UUID player) {
        AtomicReference<String> reason = new AtomicReference<>();

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM `basics_mutes` WHERE `uuid` = ?");
                statement.setString(1, player.toString());

                ResultSet results = statement.executeQuery();
                while (results.next()) {
                    reason.set(results.getString("reason"));
                }
            });
        } else {
            reason.set(plugin.getConfigManager().getPunishmentsConfig().getString("mutes." + player + ".reason"));
        }

        return reason.get();
    }

    public boolean isBlacklisted(UUID player) {
        if (storage.getType().equals(StorageType.MYSQL)) {
            AtomicBoolean isBlacklisted = new AtomicBoolean(false);

            storage.getDatabaseManager().useConnection(connection -> {
                PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_blacklists` WHERE `uuid` = ?");
                queryStatement.setString(1, player.toString());

                ResultSet result = queryStatement.executeQuery();
                while (result.next()) {
                    long expiresAt = result.getLong("expires_at");
                    if (expiresAt < TimeUtil.getCurrentTimeSeconds() && expiresAt != -1) {
                        PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM `basics_blacklists` WHERE `uuid` = ?");
                        deleteStatement.setString(1, player.toString());
                        deleteStatement.executeQuery();

                        storeUnblacklist(result.getString("ip"), null, "Expired");
                        return;
                    }

                    isBlacklisted.set(true);
                }
            });

            return isBlacklisted.get();
        } else {
            ConfigurationSection section = plugin.getConfigManager().getPunishmentsConfig().getConfigurationSection("blacklists." + player);
            if (section != null) {
                long expiresAt = section.getLong("expires_at");
                if (TimeUtil.getCurrentTimeSeconds() > expiresAt && expiresAt != -1) {
                    plugin.getConfigManager().getPunishmentsConfig().set("blacklists." + player, null);
                    plugin.getConfigManager().savePunishmentsConfig();

                    storeUnblacklist(section.getString("ip"), null, "Expired");
                    return false;
                }
            }

            return section != null;
        }
    }

    public boolean isBlacklisted(String ip) {
        if (storage.getType().equals(StorageType.MYSQL)) {
            AtomicBoolean isBlacklisted = new AtomicBoolean(false);

            storage.getDatabaseManager().useConnection(connection -> {
                PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_blacklists` WHERE `ip` = ?");
                queryStatement.setString(1, ip);

                ResultSet result = queryStatement.executeQuery();
                while (result.next()) {
                    long expiresAt = result.getLong("expires_at");
                    if (TimeUtil.getCurrentTimeSeconds() > expiresAt && expiresAt != -1) {
                        PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM `basics_blacklists` WHERE `ip` = ?");
                        deleteStatement.setString(1, ip);
                        deleteStatement.executeQuery();

                        storeUnblacklist(result.getString("ip"), null, "Expired");
                        return;
                    }

                    isBlacklisted.set(true);
                }
            });

            return isBlacklisted.get();
        } else {
            ConfigurationSection section = plugin.getConfigManager().getPunishmentsConfig().getConfigurationSection("blacklists");
            AtomicReference<String> address = new AtomicReference<>("");

            if (section == null) {
                return false;
            }

            section.getValues(false).forEach((player, data) -> {
                ConfigurationSection punishment_data = (ConfigurationSection) data;

                if (punishment_data.getString("ip").equals(ip)) {
                    long expiresAt = section.getLong("expires_at");

                    if (TimeUtil.getCurrentTimeSeconds() > expiresAt && expiresAt != -1) {
                        plugin.getConfigManager().getPunishmentsConfig().set("blacklists." + player, null);
                        plugin.getConfigManager().savePunishmentsConfig();

                        storeUnblacklist(section.getString("ip"), null, "Expired");
                        return;
                    }

                    address.set(punishment_data.getString("ip"));
                }
            });

            return address.get().equals(ip);
        }
    }

    public boolean isBanned(UUID player) {
        if (storage.getType().equals(StorageType.MYSQL)) {
            AtomicBoolean isBanned = new AtomicBoolean(false);

            storage.getDatabaseManager().useConnection(connection -> {
                PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_bans` WHERE `uuid` = ?");
                queryStatement.setString(1, player.toString());

                ResultSet result = queryStatement.executeQuery();
                while (result.next()) {
                    long expiresAt = result.getLong("expires_at");

                    if (TimeUtil.getCurrentTimeSeconds() > expiresAt && expiresAt != -1) {
                        PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM `basics_bans` WHERE `uuid` = ?");
                        deleteStatement.setString(1, player.toString());
                        deleteStatement.execute();

                        storeUnban(player, null, "Expired");
                        return;
                    }

                    isBanned.set(true);
                }
            });

            return isBanned.get();
        } else {
            ConfigurationSection section = plugin.getConfigManager().getPunishmentsConfig().getConfigurationSection("bans." + player);
            if (section != null) {
                long expiresAt = section.getLong("expires_at");
                if (TimeUtil.getCurrentTimeSeconds() > expiresAt && expiresAt != -1) {
                    plugin.getConfigManager().getPunishmentsConfig().set("bans." + player, null);
                    plugin.getConfigManager().savePunishmentsConfig();

                    storeUnban(player, null, "Expired");
                    return false;
                }
            }

            return section != null;
        }
    }

    public boolean isMuted(UUID player) {
        if (storage.getType().equals(StorageType.MYSQL)) {
            AtomicBoolean isMuted = new AtomicBoolean(false);

            storage.getDatabaseManager().useConnection(connection -> {
                PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_mutes` WHERE `uuid` = ?");
                queryStatement.setString(1, player.toString());

                ResultSet result = queryStatement.executeQuery();
                while (result.next()) {
                    long expiresAt = result.getLong("expires_at");
                    if (TimeUtil.getCurrentTimeSeconds() > expiresAt && expiresAt != -1) {
                        PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM `basics_mutes` WHERE `uuid` = ?");
                        deleteStatement.setString(1, player.toString());
                        deleteStatement.execute();

                        storeUnmute(player, null, "Expired");
                        return;
                    }

                    isMuted.set(true);
                }
            });

            return isMuted.get();
        } else {
            ConfigurationSection section = plugin.getConfigManager().getPunishmentsConfig().getConfigurationSection("mutes." + player);
            if (section != null) {
                long expiresAt = section.getLong("expires_at");
                if (expiresAt < TimeUtil.getCurrentTimeSeconds() && expiresAt != -1) {
                    plugin.getConfigManager().getPunishmentsConfig().set("mutes." + player, null);
                    plugin.getConfigManager().savePunishmentsConfig();

                    storeUnmute(player, null, "Expired");
                    return false;
                }
            }

            return section != null;
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
