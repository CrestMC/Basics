package me.blurmit.basics.scoreboard;

import me.blurmit.basics.Basics;
import me.blurmit.basics.util.placeholder.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Scoreboards {

    private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private final Objective objective;
    private final List<ScoreboardEntry> entries;
    private boolean registered;
    private final Basics plugin;

    public Scoreboards(Basics plugin) {
        this.objective = scoreboard.registerNewObjective(getRandomID(), "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        this.registered = false;
        this.entries = new ArrayList<>();
        this.plugin = plugin;
    }

    public Scoreboards getNew() {
        objective.setDisplayName(Placeholders.parsePlaceholder(plugin.getConfigManager().getScoreboardConfig().getString("title")));

        plugin.getConfigManager().getScoreboardConfig().getStringList("entries").forEach(entry -> {
            this.entries.add(entries.size(), new ScoreboardEntry(entry));
        });

        return this;
    }

    public void update(Player player) {
        objective.setDisplayName(Placeholders.parsePlaceholder(plugin.getConfigManager().getScoreboardConfig().getString("title"), true));

        if (!registered) {
            scoreboard.getEntries().forEach(scoreboard::resetScores);
            int value = entries.size();

            for (ScoreboardEntry entry : entries) {
                Team team = scoreboard.registerNewTeam("S-Team" + getRandomID());
                entry.setTeam(team);

                String entryID = ChatColor.values()[value] + "" + ChatColor.RESET;
                team.addEntry(entryID);

                updateTeamText(team, Placeholders.parsePlaceholder(entry.getValue(), player, true));

                objective.getScore(entryID).setScore(value--);
            }

            registered = true;
        } else {
            entries.forEach(entry -> updateTeamText(entry.getTeam(), Placeholders.parsePlaceholder(entry.getValue(), player, true)));
        }
    }

    public void reload() {
        clear();

        objective.setDisplayName(Placeholders.parsePlaceholder(plugin.getConfigManager().getScoreboardConfig().getString("title")));
        plugin.getConfigManager().getScoreboardConfig().getStringList("entries").forEach(entry -> {
            this.entries.add(entries.size(), new ScoreboardEntry(entry));
        });

        registered = false;
    }

    public void clear() {
        for (ScoreboardEntry entry : entries) {
            if (entry.getTeam() != null) {
                entry.getTeam().getEntries().forEach(scoreboard::resetScores);
                entry.getTeam().unregister();
            }
        }
        entries.clear();
    }

    public Scoreboards show(Player player) {
        player.setScoreboard(scoreboard);
        return this;
    }

    public Objective getObjective() {
        return objective;
    }

    private String getRandomID() {
        return String.valueOf(UUID.randomUUID()).replace("-", "").substring(25);
    }

    private void updateTeamText(Team team, String text) {
        if (text.length() > 32) {
            text = text.substring(0, 32);
        }

        if (text.length() > 16) {
            String prefix = text.substring(0, 16);

            boolean colorSplit = prefix.endsWith("§");

            if (colorSplit) {
                prefix = prefix.substring(0, 15);
            }

            String lastColor = ChatColor.getLastColors(prefix);
            String suffix = (colorSplit ? "§" : lastColor) + text.substring(16, Math.min(text.length(), 32 - lastColor.length() - (colorSplit ? 1 : 0)));

            team.setPrefix(prefix);
            team.setSuffix(suffix);
        } else {
            team.setPrefix(text);
            team.setSuffix("");
        }
    }

}
