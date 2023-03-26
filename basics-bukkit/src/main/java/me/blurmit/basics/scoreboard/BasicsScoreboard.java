package me.blurmit.basics.scoreboard;

import me.blurmit.basics.Basics;
import me.blurmit.basics.util.Placeholders;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BasicsScoreboard {

    private final Basics plugin;
    private final FileConfiguration scoreboardConfig;

    private final Scoreboard scoreboard;
    private final Objective objective;
    private final List<ScoreboardEntry> entries;
    private boolean registered;

    public BasicsScoreboard(Basics plugin) {
        this.plugin = plugin;
        this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();

        this.objective = scoreboard.registerNewObjective(getRandomID(), "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        this.registered = false;
        this.entries = new ArrayList<>();
        this.scoreboardConfig = plugin.getConfigManager().getScoreboardConfig();
    }

    public BasicsScoreboard getNew() {
        objective.setDisplayName(Placeholders.parse(scoreboardConfig.getString("title")));
        scoreboardConfig.getStringList("entries").forEach(entry -> entries.add(entries.size(), new ScoreboardEntry(entry)));

        return this;
    }

    public void update(Player player) {
        if (!registered) {
            int value = entries.size();

            for (ScoreboardEntry entry : entries) {
                Team team = scoreboard.registerNewTeam("SB-Entry-" + value);
                entry.setTeam(team);
                entry.setID(value);

                objective.getScore(updateEntry(player, entry)).setScore(value--);
            }

            registered = true;
        } else {
            for (ScoreboardEntry entry : entries) {
                updateEntry(player, entry);
            }
        }
    }

    private String updateEntry(Player player, ScoreboardEntry entry) {
        String text = getFormattedText(player, entry.getText());
        Team team = entry.getTeam();
        String id = ChatColor.values()[entry.getID()].toString();

        String oldPrefix = entry.getTeam().getPrefix();
        String prefix = id + ChatColor.RESET + text.substring(0, Math.min(text.length(), 16));
        String entryText = entry.getPlayerEntry();
        String suffix = "";

        if (text.length() > 48) {
            text = text.substring(0, 48);
        }

        if (text.length() > 16) {
            prefix = text.substring(0, 16);
            entryText = id + ChatColor.getLastColors(prefix) + text.substring(16, Math.min(32, text.length()));

            boolean splitPrefix = prefix.endsWith("§");
            if (splitPrefix) {
                prefix = text.substring(0, 15);
                entryText = text.substring(15, Math.min(32, text.length()));
            }

            String lastColors = ChatColor.getLastColors(prefix);
            entryText = lastColors + entryText;
            team.setPrefix(prefix);
        }

        if (text.length() > 32) {
            suffix = text.substring(32, Math.min(48, text.length()));

            boolean splitEntry = entryText.endsWith("§");
            if (splitEntry) {
                entryText = entryText.substring(entryText.length() - 1);
                suffix = text.substring(31, Math.min(48, text.length()));
            }

            String lastColors = ChatColor.getLastColors(entryText);
            suffix = lastColors + suffix;
            team.setSuffix(suffix);
        }

        if (entryText == null) {
            team.setPrefix(prefix);
            team.addEntry(id);
            entry.setPlayerEntry(id);
            return id;
        }

        String oldEntry = entry.getPlayerEntry();
        if (oldEntry != null) {
            if (oldEntry.equals(entryText) && prefix.equals(oldPrefix)) {
                return entryText;
            }

            scoreboard.resetScores(oldEntry);
            team.removeEntry(oldEntry);
        }

        objective.getScore(entryText).setScore(entry.getID());
        team.setPrefix(prefix);
        team.setSuffix(suffix);
        team.addEntry(entryText);
        entry.setPlayerEntry(entryText);

        return entryText;
    }

    public void reload() {
        clear();

        objective.setDisplayName(Placeholders.parse(scoreboardConfig.getString("title")));
        scoreboardConfig.getStringList("entries").forEach(entry -> {
            entries.add(entries.size(), new ScoreboardEntry(entry));
        });

        registered = false;
    }

    public void clear() {
        for (ScoreboardEntry entry : entries) {
            Team team = entry.getTeam();
            if (team != null) {
                team.getEntries().forEach(scoreboard::resetScores);
                team.getEntries().forEach(team::removeEntry);
                team.unregister();
            }
        }
        entries.clear();
    }

    public BasicsScoreboard show(Player player) {
        player.setScoreboard(scoreboard);

        return this;
    }

    public BasicsScoreboard hide(Player player) {
        player.setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());

        return this;
    }

    public Objective getObjective() {
        return objective;
    }

    private String getRandomID() {
        return String.valueOf(UUID.randomUUID()).replace("-", "").substring(25);
    }

    private String getFormattedText(Player player, String text) {
        text = Placeholders.parse(text, player, true);
        text = text.replaceAll("(§[a-f0-9]) *", "$1");
        text = text.replaceAll("(§[a-f0-9])(§[a-f0-9])+", "$2");

        return text;
    }

}
