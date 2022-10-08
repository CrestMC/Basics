package dev.blurmit.basics.util.lang;

import org.bukkit.ChatColor;

public class Prefixes {

    public static final String NONE = ChatColor.GRAY + "";
    public static final String SUCCESS = ChatColor.GREEN + "✔ " + ChatColor.GRAY;
    public static final String WARNING = ChatColor.YELLOW + "⚠ " + ChatColor.GRAY;
    public static final String ERROR = ChatColor.RED + "✖ " + ChatColor.GRAY;

    private final String prefix;

    Prefixes(String prefix) {
        this.prefix = prefix;
    }

    public String toString() {
        return prefix;
    }

}
