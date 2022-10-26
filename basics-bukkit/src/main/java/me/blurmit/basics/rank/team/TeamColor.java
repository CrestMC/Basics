package me.blurmit.basics.rank.team;

import net.md_5.bungee.api.ChatColor;

public enum TeamColor {

    RED(ChatColor.RED, false),
    DARK_RED(ChatColor.DARK_RED, false),
    ORANGE(ChatColor.GOLD, false),
    YELLOW(ChatColor.YELLOW, false),
    LIME(ChatColor.GREEN, false),
    GREEN(ChatColor.GREEN, false),
    AQUA(ChatColor.AQUA, false),
    CYAN(ChatColor.DARK_AQUA, false),
    BLUE(ChatColor.BLUE, false),
    DARK_BLUE(ChatColor.DARK_BLUE, false),
    PINK(ChatColor.LIGHT_PURPLE, false),
    PURPLE(ChatColor.DARK_PURPLE, false),
    WHITE(ChatColor.WHITE, false),
    GRAY(ChatColor.GRAY, false),
    DARK_GRAY(ChatColor.DARK_GRAY, false),
    BLACK(ChatColor.BLACK, false),
    BOLD_RED(ChatColor.RED, true),
    BOLD_DARK_RED(ChatColor.DARK_RED, true),
    BOLD_ORANGE(ChatColor.GOLD, true),
    BOLD_YELLOW(ChatColor.YELLOW, true),
    BOLD_LIME(ChatColor.GREEN, true),
    BOLD_GREEN(ChatColor.GREEN, true),
    BOLD_AQUA(ChatColor.AQUA, true),
    BOLD_CYAN(ChatColor.DARK_AQUA, true),
    BOLD_BLUE(ChatColor.BLUE, true),
    BOLD_DARK_BLUE(ChatColor.DARK_BLUE, true),
    BOLD_PINK(ChatColor.LIGHT_PURPLE, true),
    BOLD_PURPLE(ChatColor.DARK_PURPLE, true),
    BOLD_WHITE(ChatColor.WHITE, true),
    BOLD_GRAY(ChatColor.GRAY, true),
    BOLD_DARK_GRAY(ChatColor.DARK_GRAY, true),
    BOLD_BLACK(ChatColor.BLACK, true);

    private final ChatColor color;
    private final boolean bold;

    TeamColor(ChatColor color, boolean bold) {
        this.color = color;
        this.bold = bold;
    }

    public String getColor() {
        return color + "" + (bold ? ChatColor.BOLD : "");
    }

    public boolean isBold() {
        return bold;
    }

}
