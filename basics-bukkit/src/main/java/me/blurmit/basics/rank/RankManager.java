package me.blurmit.basics.rank;

import lombok.Getter;
import lombok.SneakyThrows;
import me.blurmit.basics.Basics;
import me.blurmit.basics.rank.storage.RankStorage;
import me.blurmit.basics.database.StorageType;
import me.blurmit.basics.util.Reflector;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class RankManager implements Listener {

    private Basics plugin;

    @Getter
    private RankStorage storage;
    private RankListener listener;

    @Getter
    private Map<UUID, PermissionAttachment> activeAttachments;

    public RankManager(Basics plugin) {
        if (!plugin.getConfigManager().getConfig().getBoolean("Ranks.Enabled")) {
            return;
        }

        this.plugin = plugin;
        this.storage = new RankStorage(plugin);
        this.listener = new RankListener(plugin);
        this.activeAttachments = new HashMap<>();
    }

    @Nullable
    public Rank getRankByName(String name) {
        return storage.getRanks().stream()
                .filter(rank -> rank.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public Rank getDefaultRank() {
        return storage.getRanks().stream()
                .filter(Rank::isDefault)
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public Rank getHighestRankByPriority(Player player) {
        return getRankByName(storage.getOwnedRanks().get(player.getUniqueId()).stream()
                .reduce((rank1, rank2) -> getRankByName(rank1).getPriority() > getRankByName(rank2).getPriority() ? rank1 : rank2)
                .orElse(getDefaultRank().getName() == null ? "None" : getDefaultRank().getName()));
    }

    public boolean hasPermission(Rank rank, String permission) {
        return rank.getPermissions().stream()
                .map(Permission::getName)
                .anyMatch(name -> name.equalsIgnoreCase(permission));
    }

    public boolean hasRank(UUID player, String rank) {
        return storage.getOwnedRanks().get(player).contains(rank);
    }

    public List<Permission> getRankPermissions(String rank) {
        List<Permission> permissions = new ArrayList<>();

        storage.getRanks().stream()
                .filter(group -> group.getName().equalsIgnoreCase(rank))
                .map(Rank::getPermissions)
                .forEach(permissions::addAll);

        return permissions;
    }

    public Set<Permission> getInheritedPermissions(Player player) {
        Set<Permission> permissions = new HashSet<>();

        storage.getRanks().stream()
                .filter(rank -> storage.getOwnedRanks().get(player.getUniqueId()).contains(rank.getName()) || rank.isDefault())
                .map(Rank::getPermissions)
                .forEach(permissions::addAll);

        return permissions;
    }

    @SneakyThrows
    public void clearPermissions(Player player) {
        try {
            player.removeAttachment(getActiveAttachments().get(player.getUniqueId()));
        } catch (IllegalArgumentException e) {
            Field permissibleField = Reflector.getOBCClass("entity.CraftHumanEntity").getDeclaredField("perm");
            permissibleField.setAccessible(true);
            PermissibleBase permissibleBase = (PermissibleBase) permissibleField.get(player);

            Field attachmentsField = PermissibleBase.class.getDeclaredField("attachments");
            attachmentsField.setAccessible(true);

            //noinspection unchecked
            List<PermissionAttachment> attachments = (List<PermissionAttachment>) attachmentsField.get(permissibleBase);
            attachments.stream().filter(attachment -> attachment.getPlugin().getName().equals(plugin.getName())).forEach(player::removeAttachment);
        }
    }

    public PermissionAttachment loadPermissions(Player player) {
        PermissionAttachment attachment = player.addAttachment(plugin);

        for (Permission permission : plugin.getRankManager().getInheritedPermissions(player)) {
            if (permission.getName().startsWith("group.")) {
                String rank = permission.getName().replaceFirst("group.", "");
                getInheritedGroups(rank).forEach(group -> {
                    getRankByName(group).getPermissions().forEach(perm -> {
                        attachment.setPermission(perm, perm.getDefault().equals(PermissionDefault.TRUE));
                    });
                });
                continue;
            }

            attachment.setPermission(permission, permission.getDefault().equals(PermissionDefault.TRUE));
        }

        player.updateCommands();
        return attachment;
    }

    private List<String> getInheritedGroups(String group) {
        List<String> groups = new ArrayList<>();

        getRankByName(group).getPermissions().stream()
                .map(Permission::getName)
                .filter(name -> name.startsWith("group."))
                .forEach(rank -> {
                    String rankName = rank.replaceFirst("group.", "");
                    if (getRankByName(rankName) == null) {
                        return;
                    }

                    groups.add(rankName);

                    boolean inheritsFurther = getRankByName(rankName).getPermissions().stream().anyMatch(perm -> perm.getName().startsWith("group."));
                    if (inheritsFurther) {
                        groups.addAll(getInheritedGroups(rankName));
                    }
                });

        return groups;
    }

    public void createRank(String name) {
        storage.getRanks().add(new Rank(name));

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO `basics_ranks` (`name`, `display_name`, `priority`, `default`, `prefix`, `suffix`) VALUES (?, ?, ?, ?, ?, ?)");
                statement.setString(1, name);
                statement.setString(2, name);
                statement.setInt(3, 0);
                statement.setInt(4, 0);
                statement.setString(5, "");
                statement.setString(6, "");
                statement.execute();
            });
        } else {
            plugin.getConfigManager().getRanksConfig().set("Groups." + name + ".display-name", name);
            plugin.getConfigManager().getRanksConfig().set("Groups." + name + ".prefix", "");
            plugin.getConfigManager().getRanksConfig().set("Groups." + name + ".suffix", "");
            plugin.getConfigManager().getRanksConfig().set("Groups." + name + ".priority", 0);
            plugin.getConfigManager().getRanksConfig().set("Groups." + name + ".default", false);
            plugin.getConfigManager().getRanksConfig().set("Groups." + name + ".inherits", new ArrayList<>());
            plugin.getConfigManager().getRanksConfig().set("Groups." + name + ".permissions", new ArrayList<>());
            plugin.getConfigManager().saveRanksConfig();
        }
    }

    public void deleteRank(String name) {
        storage.getOwnedRanks().keySet().forEach(owner -> revokeRank(name, owner.toString()));
        storage.getRanks().remove(getRankByName(name));

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("DELETE FROM `tags` WHERE `name` = ?");
                statement.setString(1, name);
                statement.execute();
            });
        } else {
            plugin.getConfigManager().getRanksConfig().set("Groups." + name, null);
            plugin.getConfigManager().saveRanksConfig();
        }
    }

    public void giveRank(String rank, String player, String server) {
        Player user = plugin.getServer().getPlayer(UUID.fromString(player));

        if (user != null) {
            storage.getOwnedRanks().computeIfAbsent(user.getUniqueId(), id -> new HashSet<>()).add(rank);
            getActiveAttachments().replace(user.getUniqueId(), loadPermissions(user));
        }

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO `basics_rank_members` (`rank`, `player`, `server`) VALUES (?, ?, ?)");
                statement.setString(1, rank);
                statement.setString(2, player);
                statement.setString(3, server);
                statement.execute();
            });
        } else {
            List<String> member = plugin.getConfigManager().getRanksConfig().getStringList("Groups." + rank + ".members");

            if (!member.contains(player)) {
                member.add(player);
            }

            plugin.getConfigManager().getRanksConfig().set("Groups." + rank.toLowerCase() + ".members", member);
            plugin.getConfigManager().saveRanksConfig();
        }


    }

    public void revokeRank(String rank, String player) {
        Player target = plugin.getServer().getPlayer(UUID.fromString(player));
        Rank cachedRank = getRankByName(rank);

        if (cachedRank == null) {
            return;
        }

        if (target != null) {
            clearPermissions(target);
            getActiveAttachments().replace(target.getUniqueId(), loadPermissions(target));
        }

        Set<String> members = storage.getOwnedRanks().computeIfAbsent(UUID.fromString(player), uuid -> new HashSet<>());
        members.remove(rank);

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("DELETE FROM `basics_rank_members` WHERE (`rank`, `player`) = (?, ?)");
                statement.setString(1, rank);
                statement.setString(2, player);
                statement.execute();
            });
        } else {
            List<String> member = plugin.getConfigManager().getRanksConfig().getStringList("Groups." + rank + ".members");
            member.remove(player);

            plugin.getConfigManager().getRanksConfig().set("Groups." + rank + ".members", member);
            plugin.getConfigManager().saveRanksConfig();
        }
    }

    public void giveRankPermission(String rank, String permission, String server, boolean negated) {
        Rank cachedRank = getRankByName(rank);

        if (cachedRank == null) {
            return;
        }

        Permission perm = new Permission(permission);
        perm.setDefault(negated ? PermissionDefault.FALSE : PermissionDefault.TRUE);

        Set<Permission> permissions = storage.getRanks().stream().filter(rank1 -> rank1.getName().equalsIgnoreCase(rank)).findFirst().get().getPermissions();
        permissions.add(perm);

        storage.getOwnedRanks().keySet().forEach(owner -> {
            Player player = plugin.getServer().getPlayer(owner);
            Set<String> groups = new HashSet<>(storage.getOwnedRanks().get(owner));

            groups.forEach(group -> {
                getInheritedGroups(group).forEach(inheritedGroup -> {
                    if (!groups.contains(inheritedGroup)) {
                        groups.add(inheritedGroup);
                    }
                });
            });

            if (player == null || !groups.contains(rank)) {
                return;
            }

            getActiveAttachments().get(owner).setPermission(perm, perm.getDefault().equals(PermissionDefault.TRUE));
            player.updateCommands();
        });

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO `basics_rank_permissions` (`rank`, `permission`, `server`, `negated`) VALUES (?, ?, ?, ?)");
                statement.setString(1, rank);
                statement.setString(2, permission);
                statement.setString(3, server);
                statement.setString(4, negated ? "1" : "0");
                statement.execute();
            });
        } else {
            List<String> perms = plugin.getConfigManager().getRanksConfig().getStringList("Groups." + rank + ".permissions");
            perms.add((negated ? "-" : "") + permission);

            plugin.getConfigManager().getRanksConfig().set("Groups." + rank + ".permissions", perms);
            plugin.getConfigManager().saveRanksConfig();
        }
    }

    public void removeRankPermission(String rank, String permission) {
        Rank cachedRank = getRankByName(rank);

        if (cachedRank == null) {
            return;
        }

        Set<Permission> permissions = storage.getRanks().stream().filter(rank1 -> rank1.getName().equalsIgnoreCase(rank)).findFirst().get().getPermissions();
        permissions.removeIf(perm -> perm.getName().equalsIgnoreCase(permission));

        storage.getOwnedRanks().keySet().forEach(owner -> {
            Player player = plugin.getServer().getPlayer(owner);

            if (player == null) {
                return;
            }

            clearPermissions(player);
            loadPermissions(player);

            player.updateCommands();
        });

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("DELETE FROM `basics_rank_permissions` WHERE (`rank`, `permission`) = (?, ?)");
                statement.setString(1, rank);
                statement.setString(2, permission);
                statement.execute();
            });
        } else {
            List<String> perms = plugin.getConfigManager().getRanksConfig().getStringList("Groups." + rank + ".permissions");
            perms.remove(permission);

            plugin.getConfigManager().getRanksConfig().set("Groups." + rank + ".permissions", perms);
            plugin.getConfigManager().saveRanksConfig();
        }
    }

    public void addToRankInheritance(String rank, String inheritedRank, String server) {
        Rank cachedRank = getRankByName(rank);
        Rank otherRank = getRankByName(inheritedRank);

        if (cachedRank == null || otherRank == null) {
            return;
        }

        Permission permission = new Permission("group." + inheritedRank);
        permission.setDefault(PermissionDefault.TRUE);
        cachedRank.getPermissions().add(permission);

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO `basics_rank_members` (`rank`, `player`, `server`) VALUES (?, ?, ?)");
                statement.setString(1, rank);
                statement.setString(2, "Group:" + inheritedRank);
                statement.setString(3, server);
                statement.execute();
            });
        } else {
            List<String> inheritedGroups = plugin.getConfigManager().getRanksConfig().getStringList("Groups." + rank + ".inherits");
            inheritedGroups.add(inheritedRank);

            plugin.getConfigManager().getRanksConfig().set("Groups." + rank + ".inheritedGroups", inheritedGroups);
            plugin.getConfigManager().saveRanksConfig();
        }
    }

    public void removeFromRankInheritance(String rank, String inheritedRank) {
        Rank cachedRank = getRankByName(rank);
        Rank otherRank = getRankByName(inheritedRank);

        if (cachedRank == null || otherRank == null) {
            return;
        }

        cachedRank.getPermissions().removeIf(permission -> permission.getName().equalsIgnoreCase("group." + rank));

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("DELETE FROM `basics_rank_members` WHERE (`rank`, `member`) = (?, ?)");
                statement.setString(1, rank);
                statement.setString(2, "Group:" + inheritedRank);
                statement.execute();
            });
        } else {
            List<String> inheritedGroups = plugin.getConfigManager().getRanksConfig().getStringList("Groups." + rank + ".inherits");
            inheritedGroups.remove(inheritedRank);

            plugin.getConfigManager().getRanksConfig().set("Groups." + rank + ".inheritedGroups", inheritedGroups);
            plugin.getConfigManager().saveRanksConfig();
        }
    }

    public void setRankPrefix(String rank, String prefix) {
        Rank cachedRank = getRankByName(rank);

        if (cachedRank == null) {
            return;
        }

        storage.getRanks().stream().filter(rank1 -> rank1.getName().equals(rank)).forEach(rank1 -> rank1.setPrefix(prefix));

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("UPDATE `basics_ranks` SET `prefix` = ? WHERE `rank` = ?");
                statement.setString(1, prefix);
                statement.setString(2, rank);
                statement.execute();
            });
        } else {
            plugin.getConfigManager().getRanksConfig().set("Groups." + rank + ".prefix", prefix);
            plugin.getConfigManager().saveRanksConfig();
        }
    }

    public void setRankSuffix(String rank, String suffix) {
        Rank cachedRank = getRankByName(rank);

        if (cachedRank == null) {
            return;
        }

        storage.getRanks().stream().filter(rank1 -> rank1.getName().equals(cachedRank.getName())).forEach(rank1 -> rank1.setSuffix(suffix));

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("UPDATE `basics_ranks` SET `suffix` = ? WHERE `rank` = ?");
                statement.setString(1, suffix);
                statement.setString(2, rank);
                statement.execute();
            });
        } else {
            plugin.getConfigManager().getRanksConfig().set("Groups." + rank + ".suffix", suffix);
            plugin.getConfigManager().saveRanksConfig();
        }
    }

    public void setDisplayName(String rank, String displayName) {
        Rank cachedRank = getRankByName(rank);

        if (cachedRank == null) {
            return;
        }

        storage.getRanks().stream().filter(rank1 -> rank1.getName().equals(cachedRank.getName())).forEach(rank1 -> rank1.setDisplayName(displayName));

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("UPDATE `basics_ranks` SET `display_name` = ? WHERE `rank` = ?");
                statement.setString(1, displayName);
                statement.setString(2, rank);
                statement.execute();
            });
        } else {
            plugin.getConfigManager().getRanksConfig().set("Groups." + rank + ".display-name", displayName);
            plugin.getConfigManager().saveRanksConfig();
        }
    }

    public void setRankPriority(String rank, int priority) {
        Rank cachedRank = getRankByName(rank);

        if (cachedRank == null) {
            return;
        }

        storage.getRanks().stream().filter(rank1 -> rank1.getName().equals(rank)).forEach(rank1 -> rank1.setPriority(priority));

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("UPDATE `basics_ranks` SET `priority` = ? WHERE `rank` = ?");
                statement.setInt(1, priority);
                statement.setString(2, rank);
                statement.execute();
            });
        } else {
            plugin.getConfigManager().getRanksConfig().set("Groups." + rank + ".priority", priority);
            plugin.getConfigManager().saveRanksConfig();
        }
    }

    public void setRankDefault(String rank, boolean def) {
        Rank cachedRank = getRankByName(rank);

        if (cachedRank == null) {
            return;
        }

        storage.getRanks().stream().filter(rank1 -> rank1.getName().equals(rank)).forEach(rank1 -> rank1.setDefault(def));

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("UPDATE `basics_ranks` SET `default` = ? WHERE `rank` = ?");
                statement.setInt(1, def ? 1 : 0);
                statement.setString(2, rank);
                statement.execute();
            });
        } else {
            plugin.getConfigManager().getRanksConfig().set("Groups." + rank + ".default", def);
            plugin.getConfigManager().saveRanksConfig();
        }
    }

    public void loadRankData(UUID uuid) {
        storage.getOwnedRanks().computeIfAbsent(uuid, id -> new HashSet<>());

        if (storage.getType().equals(StorageType.MYSQL)) {
            storage.getDatabaseManager().useAsynchronousConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement("SELECT `rank` FROM `basics_rank_members` WHERE `member` = ?");
                statement.setString(1, uuid.toString());
                ResultSet result = statement.executeQuery();

                while (result.next()) {
                    storage.getOwnedRanks().get(uuid).add(result.getString("rank"));
                }
            });
        } else {
            plugin.getConfigManager().getRanksConfig().getConfigurationSection("Groups").getValues(false).forEach((name, section) -> {
                if (((ConfigurationSection) section).getStringList("members").contains(uuid.toString())) {
                    storage.getOwnedRanks().get(uuid).add(name);
                }
            });

            storage.getRanks().stream().filter(Rank::isDefault).forEach(rank -> storage.getOwnedRanks().get(uuid).add(rank.getName()));
        }
    }


}
