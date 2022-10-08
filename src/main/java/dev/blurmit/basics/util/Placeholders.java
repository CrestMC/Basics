package dev.blurmit.basics.util;

import dev.blurmit.basics.event.PlaceholderRequestEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholders {

    public static String parsePlaceholder(String placeholder) {
        return parsePlaceholder(placeholder, null, null, null, null, false);
    }

    public static String parsePlaceholder(String placeholder, String... replacements) {
        return parsePlaceholder(placeholder, null, null, null, replacements, false);
    }

    public static String parsePlaceholder(String placeholder, boolean async) {
        return parsePlaceholder(placeholder, null, null, null, null, async);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender, boolean async, Object... replacements) {
        return parsePlaceholder(placeholder, sender, null, null, replacements, async);
    }

    public static String parsePlaceholder(String placeholder, Player player, boolean async, Object... replacements) {
        return parsePlaceholder(placeholder, player, null, null, replacements, async);
    }

    public static String parsePlaceholder(String placeholder, Player player, boolean async) {
        return parsePlaceholder(placeholder, player, null, null, null, async);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender, boolean async) {
        return parsePlaceholder(placeholder, sender, null, null, null, async);
    }

    public static String parsePlaceholder(String placeholder, Player player) {
        return parsePlaceholder(placeholder, player, null, null, null, false);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender) {
        return parsePlaceholder(placeholder, sender, null, null, null, false);
    }

    public static String parsePlaceholder(String placeholder, Player player, Command command, String[] args) {
        return parsePlaceholder(placeholder, player, command, args, null, false);
    }

    public static String parsePlaceholder(String placeholder, Player player, Command command, String[] args, boolean async) {
        return parsePlaceholder(placeholder, player, command, args, null, async);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender, Command command, String[] args) {
        return parsePlaceholder(placeholder, sender, command, args, null, false);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender, Command command, String[] args, boolean async) {
        return parsePlaceholder(placeholder, sender, command, args, null, async);
    }

    public static String parsePlaceholder(String placeholder, Player player, Command command, String[] args, Object[] replacements, boolean async) {
        Matcher placeholderValue = Pattern.compile("\\{(.*?)}(?!\\s*})\\s*", Pattern.DOTALL).matcher(placeholder);
        String response;

        while (placeholderValue.find()) {
            PlaceholderRequestEvent event = new PlaceholderRequestEvent(placeholderValue.group(1), player, command, args, replacements, async);
            Bukkit.getPluginManager().callEvent(event);
            response = event.getResponse();
            placeholder = placeholder.replace("{" + placeholderValue.group(1) + "}", response);
        }

        return ChatColor.translateAlternateColorCodes('&', placeholder);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender, Command command, String[] args, Object[] replacements, boolean async) {
        Matcher placeholderValue = Pattern.compile("\\{(.*?)}(?!\\s*})\\s*", Pattern.DOTALL).matcher(placeholder);
        String response;

        while (placeholderValue.find()) {
            PlaceholderRequestEvent event = new PlaceholderRequestEvent(placeholderValue.group(1), sender, command, args, replacements, async);
            Bukkit.getPluginManager().callEvent(event);
            response = event.getResponse();
            placeholder = placeholder.replace("{" + placeholderValue.group(1) + "}", response);
        }

        return ChatColor.translateAlternateColorCodes('&', placeholder);
    }

}
