package me.blurmit.basics.scoreboard;

import org.bukkit.scoreboard.Team;

public class ScoreboardEntry {

    private Team team;
    private int id;

    private String text;
    private String playerEntry;

    public ScoreboardEntry(String text) {
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setPlayerEntry(String playerEntry) {
        this.playerEntry = playerEntry;
    }

    public String getPlayerEntry() {
        return playerEntry;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

}
