package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.rank.Rank;
import me.blurmit.basics.util.lang.Messages;
import me.blurmit.basics.util.placeholder.Placeholders;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ListCommand extends CommandBase {

    private final Basics plugin;

    public ListCommand(Basics plugin) {
        super(plugin.getName());
        setName("list");
        setDescription("Sends a list of all online players, including their rank");
        setUsage("/list");
        setPermission("basics.command.list");
        setAliases(Arrays.asList("who", "online"));

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.ONLY_PLAYERS + "", sender, this, args));
            return true;
        }

        String players;
        Player player = (Player) sender;
        String ranks;

        if (plugin.getConfigManager().getConfig().getBoolean("Ranks.Enabled")) {
            ranks = plugin.getRankManager().getStorage().getRanks().stream()
                    .sorted(Comparator.comparingLong(Rank::getPriority).reversed())
                    .map(Rank::getDisplayName)
                    .collect(Collectors.joining(ChatColor.GRAY + ", " + ChatColor.RESET, ChatColor.GRAY + "", ChatColor.RESET + ""));

            players = plugin.getServer().getOnlinePlayers().stream()
                    .filter(player::canSee)
                    .sorted(Comparator.comparingLong(onlinePlayer -> -plugin.getRankManager().getHighestRankByPriority(onlinePlayer).getPriority()))
                    .map(onlinePlayer -> plugin.getRankManager().getHighestRankByPriority(onlinePlayer).getColor() + onlinePlayer.getName())
                    .collect(Collectors.joining(ChatColor.GRAY + ", " + ChatColor.RESET, ChatColor.GRAY + "", ChatColor.RESET + ""));
        } else if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            ranks = LuckPermsProvider.get().getGroupManager().getLoadedGroups().stream()
                    .sorted(Comparator.comparing(group -> -group.getWeight().getAsInt()))
                    .map(Group::getDisplayName)
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.joining(ChatColor.GRAY + ", " + ChatColor.RESET, ChatColor.GRAY + "", ChatColor.RESET + ""));

            players = plugin.getServer().getOnlinePlayers().stream()
                    .filter(player::canSee)
                    .sorted(Comparator.comparingInt(onlinePlayer -> {
                        String group = LuckPermsProvider.get().getUserManager().getUser(onlinePlayer.getUniqueId()).getPrimaryGroup();
                        return -LuckPermsProvider.get().getGroupManager().getGroup(group).getWeight().getAsInt();
                    }))
                    .map(onlinePlayer -> {
                        String group = LuckPermsProvider.get().getUserManager().getUser(onlinePlayer.getUniqueId()).getPrimaryGroup();
                        return LuckPermsProvider.get().getGroupManager().getGroup(group).getDisplayName() + " " + onlinePlayer.getName();
                    })
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.joining(ChatColor.GRAY + ", " + ChatColor.RESET, ChatColor.GRAY + "", ChatColor.RESET + ""));
        } else {
            ranks = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeams().stream()
                    .sorted()
                    .map(Team::getDisplayName)
                    .collect(Collectors.joining(ChatColor.GRAY + ", " + ChatColor.RESET, ChatColor.GRAY + "", ChatColor.RESET + ""));

            players = plugin.getServer().getOnlinePlayers().stream()
                    .filter(player::canSee)
                    .sorted()
                    .map(onlinePlayer -> {
                        Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeams().stream()
                                .filter(team1 -> team1.getPlayers().contains(onlinePlayer))
                                .findFirst()
                                .get();

                        return team.getDisplayName();
                    })
                    .collect(Collectors.joining(ChatColor.GRAY + ", " + ChatColor.RESET, ChatColor.GRAY + "", ChatColor.RESET + ""));
        }

        player.sendMessage(Placeholders.parsePlaceholder(Messages.LIST_MESSAGE + "", player, this, null, args, false, ranks, players));
        return true;
    }

}
