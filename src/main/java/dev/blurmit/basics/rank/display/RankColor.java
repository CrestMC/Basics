package dev.blurmit.basics.rank.display;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public enum RankColor {

    RED("red", "&c", "c"),
    GREEN("green", "&2", "2"),
    BLUE("blue", "&9", "9"),
    YELLOW("yellow", "&e", "e"),
    PURPLE("purple", "&5", "5"),
    ORANGE("orange", "&6", "6"),
    WHITE("white", "&f", "f"),
    BLACK("black", "&0", "0"),
    GRAY("gray", "&7", "7"),
    DARK_GRAY("dark_gray", "&8", "8"),
    LIME("light_green", "&a", "a"),
    CYAN("cyan", "&3", "3"),
    RED_BOLD("red_bold", "&c&l", "bold_red"),
    GREEN_BOLD("green_bold", "&2&l", "bold_green"),
    BLUE_BOLD("blue_bold", "&9&l", "bold_blue"),
    YELLOW_BOLD("yellow_bold", "&e&l", "bold_yellow"),
    PURPLE_BOLD("purple_bold", "&5&l", "bold_purple"),
    ORANGE_BOLD("orange_bold", "&6&l", "bold_orange"),
    WHITE_BOLD("white_bold", "&f&l", "bold_white"),
    BLACK_BOLD("black_bold", "&0&l", "bold_black"),
    GRAY_BOLD("gray_bold", "&7&l", "bold_gray"),
    DARK_GRAY_BOLD("dark_gray_bold", "&8&l", "bold_dark_gray"),
    LIME_BOLD("light_green_bold", "&a&l", "bold_light_green"),
    CYAN_BOLD("cyan_bold", "&3&l", "bold_cyan");

    private static final Map<String, RankColor> COLORS = Maps.newHashMap();

    static {
        for (RankColor colors : values()) {
            Arrays.asList(colors.color).forEach(color -> COLORS.put(color.toLowerCase(), colors));
        }
    }

    private final String[] color;

    RankColor(String... colorIn) {
        this.color = colorIn;
    }

    public static RankColor setColor(String colorIn) {
        return COLORS.get(colorIn.toLowerCase());
    }

}
