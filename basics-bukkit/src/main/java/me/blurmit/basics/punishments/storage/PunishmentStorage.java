package me.blurmit.basics.punishments.storage;

import lombok.Getter;
import me.blurmit.basics.Basics;
import me.blurmit.basics.database.DatabaseManager;
import me.blurmit.basics.database.StorageType;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

public class PunishmentStorage {

    private final Basics plugin;

    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private StorageType type;

    private static final String CREATE_BANS_TABLE = "CREATE TABLE IF NOT EXISTS `basics_bans` (`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, `uuid` CHAR(36) NOT NULL, `moderator_uuid` CHAR(36), `punished_at` bigint(20) NOT NULL, `expires_at` bigint(20) NOT NULL, `server` VARCHAR(256) NOT NULL, `reason` VARCHAR(256) NOT NULL)";
    private static final String CREATE_BLACKLISTS_TABLE = "CREATE TABLE IF NOT EXISTS `basics_blacklists` (`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, `uuid` CHAR(36) NOT NULL, `ip` VARCHAR(45) NOT NULL, `moderator_uuid` CHAR(36), `punished_at` bigint(20) NOT NULL, `expires_at` bigint(20) NOT NULL, `server` VARCHAR(256) NOT NULL, `reason` VARCHAR(256))";
    private static final String CREATE_MUTES_TABLE = "CREATE TABLE IF NOT EXISTS `basics_mutes` (`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, `uuid` CHAR(36) NOT NULL, `moderator_uuid` CHAR(36), `punished_at` bigint(20) NOT NULL, `expires_at` bigint(20) NOT NULL, `server` VARCHAR(256) NOT NULL, `reason` VARCHAR(256))";
    private static final String CREATE_HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS `basics_history` (`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, `punishment` VARCHAR(15) NOT NULL, `uuid` CHAR(36) NOT NULL, `moderator_uuid` CHAR(36), `punished_at` bigint(20) NOT NULL, `expires_at` bigint(20) NOT NULL, `server` VARCHAR(256) NOT NULL, `reason` VARCHAR(256) NOT NULL)";

    public PunishmentStorage(Basics plugin) {
        this.plugin = plugin;

        load();
    }

    private void load() {
        switch (plugin.getConfigManager().getConfig().getString("Punishments.Storage-Method").toLowerCase()) {
            case "sql":
            case "mysql": {
                plugin.getLogger().info("Using MySQL database as storage provider...");
                type = StorageType.MYSQL;

                databaseManager = new DatabaseManager(
                        plugin,
                        plugin.getConfigManager().getConfig().getString("Punishments.MySQL-Host"),
                        plugin.getConfigManager().getConfig().getString("Punishments.MySQL-Username"),
                        plugin.getConfigManager().getConfig().getString("Punishments.MySQL-Password"),
                        plugin.getConfigManager().getConfig().getString("Punishments.MySQL-Database")
                );

                databaseManager.useConnection(connection -> {
                    connection.prepareStatement(CREATE_BANS_TABLE).execute();
                    connection.prepareStatement(CREATE_BLACKLISTS_TABLE).execute();
                    connection.prepareStatement(CREATE_MUTES_TABLE).execute();
                    connection.prepareStatement(CREATE_HISTORY_TABLE).execute();
                });

                plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::reloadFromStorage, 20 * 10L, 20 * 10L);
                break;
            }
            default: {
                plugin.getLogger().info("Using punishments.yml as storage provider...");
                type = StorageType.CONFIG;
            }
        }
    }

    private void reloadFromStorage() {
        databaseManager.useAsynchronousConnection(connection -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                {
                    PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_bans` WHERE `uuid` = ?");
                    queryStatement.setString(1, player.getUniqueId().toString());

                    ResultSet results = queryStatement.executeQuery();
                    if (results.next()) {
                        String reason = results.getString("reason");
                        long until = results.getLong("expires_at");

                        if (until == -1) {
                            player.kickPlayer(Placeholders.parsePlaceholder(Messages.BAN_PERMANENT_ALERT + "", reason, "never"));
                            return;
                        }

                        long days = TimeUnit.DAYS.convert(System.currentTimeMillis() - until, TimeUnit.MILLISECONDS);
                        long hours = TimeUnit.HOURS.convert(System.currentTimeMillis() - until, TimeUnit.MILLISECONDS);
                        long minutes = TimeUnit.DAYS.convert(System.currentTimeMillis() - until, TimeUnit.MILLISECONDS);
                        long seconds = TimeUnit.DAYS.convert(System.currentTimeMillis() - until, TimeUnit.MILLISECONDS);

                        player.kickPlayer(Placeholders.parsePlaceholder(
                                Messages.BAN_TEMPORARY_ALERT + "",
                                reason, "in " + days + " days " + hours + " hours " + minutes + " minutes " + seconds + " seconds."
                        ));
                        break;
                    }
                }

                {
                    PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_mutes` WHERE `uuid` = ?");
                    queryStatement.setString(1, player.getUniqueId().toString());

                    ResultSet results = queryStatement.executeQuery();
                    if (results.next()) {
                        String reason = results.getString("reason");
                        long until = results.getLong("expires_at");

                        if (until == -1) {
                            player.sendMessage(Placeholders.parsePlaceholder(Messages.MUTE_PERMANENT_ALERT + "", reason, "never"));
                            return;
                        }

                        long days = TimeUnit.DAYS.convert(System.currentTimeMillis() - until, TimeUnit.MILLISECONDS);
                        long hours = TimeUnit.HOURS.convert(System.currentTimeMillis() - until, TimeUnit.MILLISECONDS);
                        long minutes = TimeUnit.DAYS.convert(System.currentTimeMillis() - until, TimeUnit.MILLISECONDS);
                        long seconds = TimeUnit.DAYS.convert(System.currentTimeMillis() - until, TimeUnit.MILLISECONDS);

                        player.sendMessage(Placeholders.parsePlaceholder(
                                Messages.MUTE_TEMPORARY_ALERT + "",
                                reason, "in " + days + " days " + hours + " hours " + minutes + " minutes " + seconds + " seconds."
                        ));
                        break;
                    }
                }

                {
                    PreparedStatement queryStatement = connection.prepareStatement("SELECT * FROM `basics_blacklists` WHERE `uuid` = ?");
                    queryStatement.setString(1, player.getUniqueId().toString());

                    ResultSet results = queryStatement.executeQuery();
                    if (results.next()) {
                        String ip = results.getString("ip");
                        String reason = results.getString("reason");
                        long until = results.getLong("expires_at");

                        if (until == -1) {
                            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                                if (!onlinePlayer.getAddress().getHostString().equals(ip)) {
                                    return;
                                }

                                onlinePlayer.kickPlayer(Placeholders.parsePlaceholder(
                                        Messages.BLACKLIST_PERMANENT_ALERT + "",
                                        reason,
                                        "never"));
                            }

                            break;
                        }

                        long days = TimeUnit.DAYS.convert(System.currentTimeMillis() - until, TimeUnit.MILLISECONDS);
                        long hours = TimeUnit.HOURS.convert(System.currentTimeMillis() - until, TimeUnit.MILLISECONDS);
                        long minutes = TimeUnit.DAYS.convert(System.currentTimeMillis() - until, TimeUnit.MILLISECONDS);
                        long seconds = TimeUnit.DAYS.convert(System.currentTimeMillis() - until, TimeUnit.MILLISECONDS);

                        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                            if (!onlinePlayer.getAddress().getHostString().equals(ip)) {
                                return;
                            }

                            onlinePlayer.kickPlayer(Placeholders.parsePlaceholder(
                                    Messages.BLACKLIST_TEMPORARY_ALERT + "",
                                    reason,
                                    "in " + days + " days " + hours + " hours " + minutes + " minutes " + seconds + " seconds."));
                        }
                    }
                }
            }
        });
    }

}
