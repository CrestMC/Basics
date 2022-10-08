package dev.blurmit.basics.scoreboard;

import org.bukkit.scoreboard.Team;

public class ScoreboardEntry {

    private final String value;
    private Team team;

    public ScoreboardEntry(String value) {
        this.value = value;
        this.team = null;
    }

    public ScoreboardEntry(String value, Team team) {
        this.value = value;
        this.team = team;
    }

    public String getValue() {
        return value;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

}
