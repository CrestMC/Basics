package me.blurmit.basics.rank.team;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

public enum TeamColor {

    RED(ChatColor.RED,  Material.RED_WOOL),
    DARK_RED(ChatColor.DARK_RED,  Material.REDSTONE_BLOCK),
    ORANGE(ChatColor.GOLD,  Material.ORANGE_WOOL),
    YELLOW(ChatColor.YELLOW,  Material.YELLOW_WOOL),
    LIME(ChatColor.GREEN,  Material.LIME_WOOL),
    GREEN(ChatColor.GREEN,  Material.GREEN_WOOL),
    AQUA(ChatColor.AQUA,  Material.LIGHT_BLUE_WOOL),
    CYAN(ChatColor.DARK_AQUA,  Material.CYAN_WOOL),
    BLUE(ChatColor.BLUE,  Material.BLUE_WOOL),
    DARK_BLUE(ChatColor.DARK_BLUE,  Material.LAPIS_BLOCK),
    PINK(ChatColor.LIGHT_PURPLE,  Material.PINK_WOOL),
    PURPLE(ChatColor.DARK_PURPLE,  Material.PURPLE_WOOL),
    WHITE(ChatColor.WHITE,  Material.QUARTZ_BLOCK),
    GRAY(ChatColor.GRAY,  Material.LIGHT_GRAY_WOOL),
    DARK_GRAY(ChatColor.DARK_GRAY,  Material.GRAY_WOOL),
    BLACK(ChatColor.BLACK, Material.BLACK_WOOL);

    private final ChatColor color;
    private final Material material;

    TeamColor(ChatColor color, Material material) {
        this.color = color;
        this.material = material;
    }

    public String getColor() {
        return color + "";
    }

    public Material getMaterial() {
        return material;
    }
}
