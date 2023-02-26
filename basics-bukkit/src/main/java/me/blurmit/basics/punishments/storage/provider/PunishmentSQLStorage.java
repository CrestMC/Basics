package me.blurmit.basics.punishments.storage.provider;

import lombok.Getter;
import me.blurmit.basics.Basics;
import me.blurmit.basics.database.DatabaseManager;
import me.blurmit.basics.punishments.PunishmentType;
import me.blurmit.basics.punishments.data.PunishmentData;
import me.blurmit.basics.punishments.storage.provider.PunishmentStorageProvider;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.PluginMessageUtil;
import me.blurmit.basics.util.TimeUtil;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class PunishmentSQLStorage extends PunishmentStorageProvider {

    @Getter
    private DatabaseManager database;
    private final Basics plugin;

    private static final String CREATE_BANS_TABLE = "CREATE TABLE IF NOT EXISTS `basics_bans` (`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, `target` CHAR(36) NOT NULL, `moderator` CHAR(36), `punished_at` bigint(20) NOT NULL, `expires_at` bigint(20) NOT NULL, `server` VARCHAR(256) NOT NULL, `reason` VARCHAR(256) NOT NULL)";
    private static final String CREATE_BLACKLISTS_TABLE = "CREATE TABLE IF NOT EXISTS `basics_blacklists` (`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, `target` CHAR(36) NOT NULL, `ip` VARCHAR(45) NOT NULL, `moderator` CHAR(36), `punished_at` bigint(20) NOT NULL, `expires_at` bigint(20) NOT NULL, `server` VARCHAR(256) NOT NULL, `reason` VARCHAR(256))";
    private static final String CREATE_MUTES_TABLE = "CREATE TABLE IF NOT EXISTS `basics_mutes` (`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, `target` CHAR(36) NOT NULL, `moderator` CHAR(36), `punished_at` bigint(20) NOT NULL, `expires_at` bigint(20) NOT NULL, `server` VARCHAR(256) NOT NULL, `reason` VARCHAR(256))";
    private static final String CREATE_HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS `basics_history` (`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, `punishment` VARCHAR(15) NOT NULL, `target` CHAR(36) NOT NULL, `moderator` CHAR(36), `punished_at` bigint(20) NOT NULL, `expires_at` bigint(20) NOT NULL, `server` VARCHAR(256) NOT NULL, `reason` VARCHAR(256) NOT NULL)";

    public PunishmentSQLStorage(Basics plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when the storage provider is ready to be loaded.
     * <p>Any necessary database components should be created here.</p>
     */
    @Override
    public void load() {
        Configuration config = plugin.getConfigManager().getConfig();
        database = new DatabaseManager(plugin, config.getString("Punishments.MySQL-Host"), config.getString("Punishments.MySQL-Username"), config.getString("Punishments.MySQL-Password"), config.getString("Punishments.MySQL-Database"));

        getDatabase().useConnection(connection -> {
            connection.prepareStatement(CREATE_BANS_TABLE).execute();
            connection.prepareStatement(CREATE_BLACKLISTS_TABLE).execute();
            connection.prepareStatement(CREATE_MUTES_TABLE).execute();
            connection.prepareStatement(CREATE_HISTORY_TABLE).execute();
        });

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::reloadFromStorage, 300L, 300L);
    }

    /**
     * Stores a ban in the specified storage provider
     *
     * @param target    Target to be banned
     * @param moderator Moderator who issued the ban
     * @param reason    Reason why the punishment is being issued
     * @param punishedAt The unix-epoch time stamp that this punishment was made
     * @param expiresAt The unix-epoch time stamp that this punishment will expire
     * @param server    The server this punishment was issued on
     */
    @Override
    public void storeBan(UUID target, UUID moderator, String reason, long punishedAt, long expiresAt, String server) {
        getDatabase().useAsynchronousConnection(connection -> {
            PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_bans` WHERE `target` = ?");
            queryStatement.setString(1, target.toString());

            // Delete current ban from database if it exists.
            ResultSet result = queryStatement.executeQuery();
            if (result.next()) {
                storeUnban(target, moderator, "Ban Override", server);
                storeBan(target, moderator, reason, punishedAt, expiresAt, server);
                return;
            }

            PreparedStatement banStatement = connection.prepareStatement("INSERT IGNORE INTO `basics_bans` (`target`, `moderator`, `punished_at`, `expires_at`, `server`, `reason`) VALUES (?, ?, ?, ?, ?, ?)");
            banStatement.setString(1, target.toString());
            banStatement.setString(2, moderator == null ? "null" : moderator.toString());
            banStatement.setLong(3, punishedAt);
            banStatement.setLong(4, expiresAt);
            banStatement.setString(5, server);
            banStatement.setString(6, reason);
            banStatement.execute();
        });
    }

    /**
     * Stores an unban in the specified storage provider
     *
     * @param target    Target to be unbanned
     * @param moderator Moderator who issued to unban
     * @param reason    Reason why to unban
     * @param server    The server this specific unban was issued on
     */
    @Override
    public void storeUnban(UUID target, UUID moderator, String reason, String server) {
        getDatabase().useAsynchronousConnection(connection -> {
            PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_bans` WHERE `target` = ?");
            queryStatement.setString(1, target + "");

            ResultSet results = queryStatement.executeQuery();
            while (results.next()) {
                long punishedAt = results.getLong("punished_at");
                long expiresAt = TimeUtil.getCurrentTimeSeconds();
                storePunishment(PunishmentType.UNBAN, target, moderator, reason, punishedAt, expiresAt, server);
            }

            PreparedStatement unbanStatement = connection.prepareStatement("DELETE FROM `basics_bans` WHERE `target` = ?");
            unbanStatement.setString(1, target + "");
            unbanStatement.execute();
        });
    }

    /**
     * Stores a blacklist in the specified storage provider
     *
     * @param target    Target to be banned
     * @param moderator Moderator who issued the ban
     * @param reason    Reason why the punishment is being issued
     * @param punishedAt The unix-epoch time stamp that this punishment was made
     * @param expiresAt The unix-epoch time stamp that this punishment will expire
     * @param server    The server this punishment was issued on
     */
    @Override
    public void storeBlacklist(UUID target, UUID moderator, String reason, long punishedAt, long expiresAt, String server) {
        getDatabase().useAsynchronousConnection(connection -> {
            PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_blacklists` WHERE `target` = ?");
            queryStatement.setString(1, target + "");

            // Delete current blacklist from database if it exists.
            ResultSet result = queryStatement.executeQuery();
            if (result.next()) {
                storeUnblacklist(target, moderator, "Blacklist Override", server);
                storeBlacklist(target, moderator, reason, punishedAt, expiresAt, server);
                return;
            }

            PreparedStatement blacklistStatement = connection.prepareStatement("INSERT IGNORE INTO `basics_blacklists` (`target`, `ip`, `moderator`, `punished_at`, `expires_at`, `server`, `reason`) VALUES (?, ?, ?, ?, ?, ?, ?)");
            blacklistStatement.setString(1, target + "");
            blacklistStatement.setString(2, "0.0.0.0");
            blacklistStatement.setString(3, moderator == null ? "null" : moderator + "");
            blacklistStatement.setLong(4, TimeUtil.getCurrentTimeSeconds());
            blacklistStatement.setLong(5, expiresAt);
            blacklistStatement.setString(6, server);
            blacklistStatement.setString(7, reason);
            blacklistStatement.execute();
        });
    }

    /**
     * Stores an un-blacklist in the specified storage provider
     *
     * @param target    Target to be un-blacklisted
     * @param moderator Moderator who issued the un-blacklist
     * @param reason    Reason to unban
     * @param server    The server this un-blacklist was issued on
     */
    @Override
    public void storeUnblacklist(UUID target, UUID moderator, String reason, String server) {
        getDatabase().useAsynchronousConnection(connection -> {
            PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_blacklists` WHERE `ip` = ?");
            queryStatement.setString(1, "0.0.0.0");

            ResultSet results = queryStatement.executeQuery();
            while (results.next()) {
                long punishedAt = TimeUtil.getCurrentTimeSeconds();
                long expiresAt = results.getLong("punished_at");
                storePunishment(PunishmentType.UNBLACKLIST, target, moderator, reason, punishedAt, expiresAt, server);
            }

            PreparedStatement unbanStatement = connection.prepareStatement("DELETE FROM `basics_blacklists` WHERE `ip` = ?");
            unbanStatement.setString(1, "0.0.0.0");
            unbanStatement.execute();
        });
    }

    /**
     * Stores a mute in the specified storage provider
     *
     * @param target    Target to be banned
     * @param moderator Moderator who issued the ban
     * @param reason    Reason why the punishment is being issued
     * @param punishedAt The unix-epoch time stamp that this punishment was made
     * @param expiresAt The unix-epoch time stamp that this punishment will expire
     * @param server    The server this punishment was issued on
     */
    @Override
    public void storeMute(UUID target, UUID moderator, String reason, long punishedAt, long expiresAt, String server) {
        getDatabase().useAsynchronousConnection(connection -> {
            PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_mutes` WHERE `target` = ?");
            queryStatement.setString(1, target + "");

            // Delete current mute from database if it exists.
            ResultSet result = queryStatement.executeQuery();
            if (result.next()) {
                storeUnmute(target, moderator, "Mute Override", server);
                storeMute(target, moderator, reason, punishedAt, expiresAt, server);
                return;
            }

            PreparedStatement muteStatement = connection.prepareStatement("INSERT IGNORE INTO `basics_mutes` (`target`, `moderator`, `punished_at`, `expires_at`, `server`, `reason`) VALUES (?, ?, ?, ?, ?, ?)");
            muteStatement.setString(1, target + "");
            muteStatement.setString(2, moderator == null ? "null" : moderator + "");
            muteStatement.setLong(3, TimeUtil.getCurrentTimeSeconds());
            muteStatement.setLong(4, expiresAt);
            muteStatement.setString(5, server);
            muteStatement.setString(6, reason);
            muteStatement.execute();
        });
    }

    /**
     * Stores an unmute in the specified storage provider
     *
     * @param target    Target to be unmuted
     * @param moderator Moderator who issued the unmute
     * @param reason    Reason to unmute
     * @param server    The server this unmute was issued on
     */
    @Override
    public void storeUnmute(UUID target, UUID moderator, String reason, String server) {
        getDatabase().useAsynchronousConnection(connection -> {
            PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_mutes` WHERE `target` = ?");
            queryStatement.setString(1, target + "");

            ResultSet results = queryStatement.executeQuery();
            while (results.next()) {
                long punishedAt = results.getLong("punished_at");
                long expiresAt = TimeUtil.getCurrentTimeSeconds();
                storePunishment(PunishmentType.UNMUTE, target, moderator, reason, punishedAt, expiresAt, server);
            }

            PreparedStatement unbanStatement = connection.prepareStatement("DELETE FROM `basics_mutes` WHERE `target` = ?");
            unbanStatement.setString(1, target + "");
            unbanStatement.execute();
        });
    }

    /**
     * Stores a punishment in the specified storage provider
     *
     * @param type      The punishment type to be issued
     * @param target    Target to be banned
     * @param moderator Moderator who issued the ban
     * @param reason    Reason why the punishment is being issued
     * @param punishedAt The unix-epoch time stamp that this punishment was made
     * @param expiresAt The unix-epoch time stamp that this punishment will expire
     * @param server    The server this punishment was issued on
     */
    @Override
    public void storePunishment(PunishmentType type, UUID target, UUID moderator, String reason, long punishedAt, long expiresAt, String server) {
        getDatabase().useAsynchronousConnection(connection -> {
            PreparedStatement historyStatement = connection.prepareStatement("INSERT IGNORE INTO `basics_history` (`punishment`, `target`, `moderator`, `punished_at`, `expires_at`, `server`, `reason`) VALUES (?, ?, ?, ?, ?, ?, ?)");
            historyStatement.setString(1, type.name());
            historyStatement.setString(2, target + "");
            historyStatement.setString(3, moderator == null ? "null" : moderator + "");
            historyStatement.setLong(4, punishedAt);
            historyStatement.setLong(5, expiresAt);
            historyStatement.setString(6, server);
            historyStatement.setString(7, reason);
            historyStatement.execute();
        });
    }

    /**
     * Gets the target's punishment data directly from the specified storage provider for the {@link PunishmentType} provided.
     * <p>If the target has no active punishments of that type, this will return null.</p>
     * <p>The {@link CompletableFuture<PunishmentData>} will always be completed, regardless of the target's punishment state.</p>
     * @param type   The punishment type that should be looked up.
     * @param target The target in which the data will be derived from.
     * @return A {@link CompletableFuture<PunishmentData>} containing all data about the {@link PunishmentType} provided.
     */
    @Override
    public CompletableFuture<PunishmentData> getPunishmentData(PunishmentType type, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            AtomicReference<PunishmentData> data = new AtomicReference<>();
            getDatabase().useConnection(connection -> {
                // Not really worried about SQL injection as there's no way for a player to input any string other than the predefined types (which they can't edit)
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM `basics_" + type + "` WHERE `target` = ?");
                statement.setString(1, target + "");

                ResultSet results = statement.executeQuery();
                if (!results.next()) {
                    data.set(null);
                    return;
                }

                data.set(PunishmentData.of(results));
            });

            return data.get();
        });
    }

    private void reloadFromStorage() {
        getDatabase().useAsynchronousConnection(connection -> {
            updateBans(connection);
            updateBlacklists(connection);
            updateMutes(connection);
        });
    }

    private void updateBlacklists(Connection connection) throws SQLException {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (plugin.getPunishmentManager().getBannedPlayers().containsKey(player.getUniqueId())) {
                continue;
            }

            PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_blacklists` WHERE `target` = ?");
            queryStatement.setString(1, player.getUniqueId().toString());

            ResultSet results = queryStatement.executeQuery();
            if (results.next()) {
                String ip = results.getString("ip");
                String reason = results.getString("reason");
                long expiresAt = results.getLong("expires_at");

                plugin.getServer().getOnlinePlayers().stream()
                        .filter(onlinePlayer -> {
                            InetSocketAddress address = onlinePlayer.getAddress();
                            if (address == null) {
                                return false;
                            }

                            return address.getHostString().equals(ip);
                        })
                        .forEach(onlinePlayer -> {
                            if (expiresAt == -1) {
                                PluginMessageUtil.sendData("BungeeCord", "KickPlayer", onlinePlayer.getName(), Placeholders.parsePlaceholder(
                                        Messages.BAN_PERMANENT_ALERT + "",
                                        true,
                                        reason,
                                        "never"
                                ));
                                return;
                            }

                            PluginMessageUtil.sendData("BungeeCord", "KickPlayer", onlinePlayer.getName(), Placeholders.parsePlaceholder(
                                    Messages.BAN_TEMPORARY_ALERT + "",
                                    true,
                                    reason,
                                    TimeUtil.getHowLongUntil(expiresAt)
                            ));
                        });
            }
        }
    }

    private void updateBans(Connection connection) throws SQLException {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (plugin.getPunishmentManager().getBannedPlayers().containsKey(player.getUniqueId())) {
                continue;
            }

            PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_bans` WHERE `target` = ?");
            queryStatement.setString(1, player.getUniqueId().toString());

            ResultSet results = queryStatement.executeQuery();
            if (results.next()) {
                String reason = results.getString("reason");
                long expiresAt = results.getLong("expires_at");

                if (expiresAt == -1) {
                    PluginMessageUtil.sendData("BungeeCord", "KickPlayer", player.getName(), Placeholders.parsePlaceholder(
                            Messages.BAN_PERMANENT_ALERT + "",
                            true,
                            reason,
                            "never"
                    ));
                    return;
                }

                PluginMessageUtil.sendData("BungeeCord", "KickPlayer", player.getName(), Placeholders.parsePlaceholder(
                        Messages.BAN_TEMPORARY_ALERT + "",
                        true,
                        reason,
                        TimeUtil.getHowLongUntil(expiresAt)
                ));
            }
        }
    }

    private void updateMutes(Connection connection) throws SQLException {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (plugin.getPunishmentManager().getMutedPlayers().containsKey(player.getUniqueId())) {
                continue;
            }

            PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_mutes` WHERE `target` = ?");
            queryStatement.setString(1, player.getUniqueId().toString());

            ResultSet results = queryStatement.executeQuery();
            if (results.next()) {
                String reason = results.getString("reason");
                long expiresAt = results.getLong("expires_at");
                long timeLeft = expiresAt - TimeUtil.getCurrentTimeSeconds();
                BukkitTask task = null;
                if (timeLeft != -1) {
                    task = plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> plugin.getPunishmentManager().storeUnmute(player.getUniqueId(), null, "Expired"), timeLeft * 20L);
                }

                plugin.getPunishmentManager().getMutedPlayers().put(player.getUniqueId(), task);

                if (expiresAt == -1) {
                    player.sendMessage(Placeholders.parsePlaceholder(
                            Messages.MUTE_PERMANENT_ALERT + "",
                            reason,
                            "never"
                    ));
                    return;
                }

                player.sendMessage(Placeholders.parsePlaceholder(
                        Messages.MUTE_TEMPORARY_ALERT + "",
                        reason,
                        TimeUtil.getHowLongUntil(expiresAt)
                ));
                break;
            }
        }
    }

}
