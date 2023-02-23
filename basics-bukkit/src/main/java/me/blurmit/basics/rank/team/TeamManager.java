package me.blurmit.basics.rank.team;

import me.blurmit.basics.Basics;
import me.blurmit.basics.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamManager {

    private final Basics plugin;

    public TeamManager(Basics plugin) {
        this.plugin = plugin;
    }

    public void setupTeams(Player player) {
        Scoreboard scoreboard = player.getScoreboard();

        for (Rank rank : plugin.getRankManager().getStorage().getRanks()) {
            String color = ChatColor.translateAlternateColorCodes('&', rank.getColor());
            String displayName = ChatColor.translateAlternateColorCodes('&', rank.getDisplayName());

            Team team;
            try {
                team = scoreboard.registerNewTeam(rank.getName());
            } catch (IllegalArgumentException e) {
                continue;
            }

            team.setPrefix(color);
            team.setDisplayName(displayName);
            team.setColor(ChatColor.BOLD);
        }
    }

    public void addPlayerToRankTeam(Player player, String rank) {
        plugin.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
            Team team = onlinePlayer.getScoreboard().getTeam(rank);

            if (team == null) {
                return;
            }

            team.addPlayer(player);
        });
    }

    public void removePlayerFromRankTeam(Player player, String rank) {
        plugin.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
            Team team = onlinePlayer.getScoreboard().getTeam(rank);

            if (team == null) {
                return;
            }

            team.removePlayer(player);
        });
    }

}
