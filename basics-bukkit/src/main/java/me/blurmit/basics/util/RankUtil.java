package me.blurmit.basics.util;

import me.blurmit.basics.Basics;
import me.blurmit.basics.rank.Rank;
import me.blurmit.basics.util.lang.Messages;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.UserManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

public class RankUtil {

    private static final Basics plugin = JavaPlugin.getPlugin(Basics.class);

    public static String getColoredName(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return Placeholders.parsePlaceholder(Messages.CONSOLE_NAME + "", true);
        }

        Player player = (Player) sender;
        return getColoredName(player.getUniqueId());
    }

    public static String getColoredName(UUID uuid) {
        return getColor(uuid) + UUIDUtil.getName(uuid);
    }

    public static String getColor(UUID uuid) {
        String color = ChatColor.GRAY + "";

        if (plugin.getRankManager() != null) {
            color = plugin.getRankManager().getHighestRankByPriority(uuid).getColor();
        } else if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            LuckPerms luckPerms = LuckPermsProvider.get();
            GroupManager groupManager = luckPerms.getGroupManager();
            UserManager userManager = luckPerms.getUserManager();
            Group primaryGroup = groupManager.getGroup(userManager.getUser(uuid).getPrimaryGroup());
            color = primaryGroup.getCachedData().getMetaData().getMetaValue("color");
        }

        return color;
    }

    public static int getPriority(UUID uuid) {
        int priority = 0;

        if (plugin.getRankManager() != null) {
            priority = (int) plugin.getRankManager().getHighestRankByPriority(uuid).getPriority();
        } else if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            LuckPerms luckPerms = LuckPermsProvider.get();
            GroupManager groupManager = luckPerms.getGroupManager();
            UserManager userManager = luckPerms.getUserManager();
            Group primaryGroup = groupManager.getGroup(userManager.getUser(uuid).getPrimaryGroup());
            priority = primaryGroup.getWeight().getAsInt();
        }

        return priority;
    }

    public static String getOrderedRanks() {
        String ranks = "No Ranks Found";

        if (plugin.getRankManager() != null) {
            ranks = plugin.getRankManager().getStorage().getRanks().stream()
                    .sorted(Comparator.comparingLong(Rank::getPriority).reversed())
                    .map(Rank::getDisplayName)
                    .collect(Collectors.joining(ChatColor.GRAY + ", " + ChatColor.RESET, ChatColor.GRAY + "", ChatColor.RESET + ""));
        } else if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            LuckPerms luckPerms = LuckPermsProvider.get();
            GroupManager groupManager = luckPerms.getGroupManager();

            ranks = groupManager.getLoadedGroups()
                    .stream()
                    .sorted(Comparator.comparingInt(group -> group.getWeight().getAsInt()))
                    .map(Group::getDisplayName)
                    .collect(Collectors.joining(ChatColor.GRAY + ", " + ChatColor.RESET, ChatColor.GRAY + "", ChatColor.RESET + ""));
        }

        return ranks;
    }

}
