package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.rank.Rank;
import me.blurmit.basics.rank.RankManager;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VanishCommand extends CommandBase {

    private final Basics plugin;

    private final Map<UUID, Integer> vanishedPlayers;

    public VanishCommand(Basics plugin) {
        super(plugin.getName());
        setName("vanish");
        setDescription("Makes you completely invisible to other players");
        setUsage("/vanish [player|rank]");
        setPermission("basics.command.vanish");

        this.plugin = plugin;
        this.vanishedPlayers = new HashMap<>();
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parse(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parse(Messages.ONLY_PLAYERS + "", sender, this, args));
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        // No arguments provided, just handle vanish-self logic
        if (args.length == 0) {
            // If the player is already vanished, unvanish them
            if (vanishedPlayers.containsKey(uuid)) {
                unvanishPlayer(player);
                return true;
            }

            // Player is not already vanished, vanish them
            vanishPlayer(player);
            return true;
        }

        RankManager rankManager = plugin.getRankManager();

        // Check if the built-in rank permission system is loaded, if so, vanish by that
        if (rankManager != null) {
            Rank rank = rankManager.getRankByName(args[0]);

            if (rank != null) {
                vanishPlayer(player, rank);
                return true;
            }
        }

        // Check if LuckPerms is loaded, if so, vanish by that
        try {
            Group group = LuckPermsProvider.get().getGroupManager().getGroup(args[0]);

            if (group != null) {
                vanishPlayer(player, group.getWeight().getAsInt());
                return true;
            }
        } catch (IllegalStateException | NoClassDefFoundError ignored) {
            // If neither LuckPerms nor the built-in rank system are loaded, parse argument as a number
            long level;

            try {
                level = Integer.parseInt(args[0]);

                vanishPlayer(player, level);
            } catch (NumberFormatException ignored1) {
                // If argument is not a number either, check if it's a player
                Player target = plugin.getServer().getPlayer(args[0]);

                // Check if target is not a player, stop attempting to parse
                if (target == null) {
                    // Not a player either
                }

                vanishPlayer(player);
            }
        }

        return true;
    }

    /**
     * Makes the {@link Player} invisible to all other online {@link Player}s
     * @param player The player to be hidden
     */
    public void vanishPlayer(Player player) {
        vanishPlayer(player, "basics.vanish.see");
    }

    /**
     * Makes the {@link Player} invisible to all other online {@link Player}s unless said online {@link Player} has the "basics.vanish.see.[level]" {@link org.bukkit.permissions.Permission}
     * @param player The player to be hidden
     * @param level The level in which to hide the {@link Player} with. All players with the "basics.vanish.see.[level]" {@link org.bukkit.permissions.Permission} or below will have the specified player hidden
     */
    public void vanishPlayer(Player player, long level) {
        vanishPlayer(player, "basics.vanish." + level);
    }

    /**
     * Makes the {@link Player} invisible to all other online {@link Player}s unless said online {@link Player} has the {@link Rank} specified
     * @param player The player to be hidden
     * @param rank The rank which will be checked before the {@link Player} is hidden
     */
    public void vanishPlayer(Player player, Rank rank) {
        RankManager rankManager = plugin.getRankManager();

        plugin.getServer().getOnlinePlayers().stream()
                .filter(onlinePlayer -> !rankManager.hasRank(onlinePlayer.getUniqueId(), rank.getName()))
                .forEach(onlinePlayer -> onlinePlayer.hidePlayer(plugin, player));
    }

    /**
     * Makes the {@link Player} invisible to all other online {@link Player}s unless said online {@link Player} has the {@link org.bukkit.permissions.Permission} specified
     * @param player The player to be hidden
     * @param permission The permission which will be checked for before the {@link Player} is hidden
     */
    public void vanishPlayer(Player player, String permission) {
        plugin.getServer().getOnlinePlayers().stream()
                .filter(onlinePlayer -> !onlinePlayer.hasPermission(permission))
                .forEach(onlinePlayer -> onlinePlayer.hidePlayer(plugin, player));
    }

    /**
     * Removes the vanished status from the {@link Player}
     * @param player The player to unvanish
     */
    public void unvanishPlayer(Player player) {
        vanishedPlayers.remove(player.getUniqueId());

        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            onlinePlayer.showPlayer(plugin, player);
        }
    }


}
