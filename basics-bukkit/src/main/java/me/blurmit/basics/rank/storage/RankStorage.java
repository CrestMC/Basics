package me.blurmit.basics.rank.storage;

import lombok.Getter;
import me.blurmit.basics.Basics;
import me.blurmit.basics.database.DatabaseManager;
import me.blurmit.basics.database.StorageType;
import me.blurmit.basics.rank.Rank;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class RankStorage {

    private final Basics plugin;

    @Getter
    private Set<Rank> ranks;
    @Getter
    private final Map<UUID, Set<String>> ownedRanks;

    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private StorageType type;

    private static final String CREATE_RANKS_TABLE = "CREATE TABLE IF NOT EXISTS `basics_ranks` (`name` VARCHAR(64) NOT NULL PRIMARY KEY, `display_name` VARCHAR(255) NOTNULL, `priority` INT NOT NULL, `default` TINYINT(1) NOT NULL, `prefix` VARCHAR(255) NOT NULL, `suffix` VARCHAR(255) NOT NULL)";
    private static final String CREATE_RANK_PERMISSION_TABLE = "CREATE TABLE IF NOT EXISTS `basics_rank_permissions` (`rank` VARCHAR(64) NOT NULL, `permission` VARCHAR(64) NOT NULL, `server` VARCHAR(64) NOT NULL, `negated` TINYINT(1), `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY)";
    private static final String CREATE_RANK_MEMBER_TABLE = "CREATE TABLE IF NOT EXISTS `basics_rank_members` (`rank` VARCHAR(64) NOT NULL, `member` VARCHAR(36) NOT NULL, `server` VARCHAR(64) NOT NULL, `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY)";

    public RankStorage(Basics plugin) {
        this.plugin = plugin;

        this.ranks = new HashSet<>();
        this.ownedRanks = new HashMap<>();

        load();
    }

    private void load() {
        switch (plugin.getConfigManager().getConfig().getString("Ranks.Storage-Method").toLowerCase()) {
            case "sql":
            case "mysql": {
                plugin.getLogger().info("Using MySQL database as storage provider...");
                type = StorageType.MYSQL;

                databaseManager = new DatabaseManager(
                        plugin,
                        plugin.getConfigManager().getConfig().getString("Ranks.MySQL-Host"),
                        plugin.getConfigManager().getConfig().getString("Ranks.MySQL-Username"),
                        plugin.getConfigManager().getConfig().getString("Ranks.MySQL-Password"),
                        plugin.getConfigManager().getConfig().getString("Ranks.MySQL-Database")
                );

                databaseManager.useConnection(connection -> {
                    connection.prepareStatement(CREATE_RANKS_TABLE).execute();
                    connection.prepareStatement(CREATE_RANK_PERMISSION_TABLE).execute();
                    connection.prepareStatement(CREATE_RANK_MEMBER_TABLE).execute();
                });
                break;
            }
            default: {
                plugin.getLogger().info("Using ranks.yml as storage provider...");
                type = StorageType.CONFIG;
            }
        }

        if (type.equals(StorageType.MYSQL)) {
            plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::loadRanks, 0L, 10 * 20L);
        } else {
            loadRanks();
        }
    }

    private void loadRanks() {
        switch (type) {
            case CONFIG: {
                Set<Rank> ranks = new HashSet<>();

                plugin.getConfigManager().getRanksConfig().getConfigurationSection("Groups").getValues(false).forEach((name, section) -> {
                    ConfigurationSection data = (ConfigurationSection) section;
                    Rank rank = new Rank(name, data.getLong("priority"), data.getBoolean("default"));
                    rank.setDisplayName(data.getString("display-name") == null ? name : data.getString("display-name"));
                    rank.setPrefix(data.getString("prefix"));
                    rank.setSuffix(data.getString("suffix"));
                    data.getStringList("permissions").forEach(permission -> {
                        Permission perm = new Permission(permission);
                        perm.setDefault(permission.startsWith("-") ? PermissionDefault.FALSE : PermissionDefault.TRUE);
                        rank.getPermissions().add(perm);
                    });
                    data.getStringList("members").forEach(member -> {
                        if (ownedRanks.get(UUID.fromString(member)) == null) {
                            return;
                        }

                        ownedRanks.get(UUID.fromString(member)).add(name);
                    });
                    data.getStringList("inherits").forEach(group -> {
                        Permission permission = new Permission("group." + group);
                        permission.setDefault(PermissionDefault.TRUE);
                        rank.getPermissions().add(permission);
                    });

                    ranks.add(rank);
                });

                this.ranks = ranks;
                break;
            }
            case MYSQL: {
                Set<Rank> ranks = new HashSet<>();

                databaseManager.useAsynchronousConnection(connection -> {
                    {
                        PreparedStatement statement = connection.prepareStatement("SELECT * FROM `basics_ranks`");
                        ResultSet result = statement.executeQuery();

                        while (result.next()) {
                            String name = result.getString("name");
                            String displayName = result.getString("display-name");
                            int priority = result.getInt("priority");
                            int def = result.getInt("default");
                            String prefix = result.getString("prefix");
                            String suffix = result.getString("suffix");

                            Rank rank = new Rank(name, priority, def == 1);
                            rank.setPrefix(prefix);
                            rank.setSuffix(suffix);
                            rank.setDisplayName(displayName);

                            ranks.add(rank);
                        }
                    }

                    {
                        for (Rank rank : ranks) {
                            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `basics_rank_permissions` WHERE `rank` = ?");
                            statement.setString(1, rank.getName());
                            ResultSet result = statement.executeQuery();

                            while (result.next()) {
                                Permission permission = new Permission(result.getString("permission"));
                                permission.setDefault(result.getInt("negated") == 1 ? PermissionDefault.FALSE : PermissionDefault.TRUE);
                                rank.getPermissions().add(permission);
                            }
                        }
                    }
                });

                this.ranks = ranks;
            }
        }
    }

}
