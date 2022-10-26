package me.blurmit.basics.util.placeholder;

import me.blurmit.basics.command.defined.SubCommand;
import me.blurmit.basics.events.PlaceholderRequestEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholders {

    public static String parsePlaceholder(String placeholder) {
        return parsePlaceholder(placeholder, null, null, null, null, null, false);
    }

    public static String parsePlaceholder(String placeholder, String... replacements) {
        return parsePlaceholder(placeholder, null, null, null, null, replacements, false);
    }

    public static String parsePlaceholder(String placeholder, boolean async, String... replacements) {
        return parsePlaceholder(placeholder, null, null, null, null, replacements, async);
    }

    public static String parsePlaceholder(String placeholder, boolean async) {
        return parsePlaceholder(placeholder, null, null, null, null, null, async);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender, boolean async, Object... replacements) {
        return parsePlaceholder(placeholder, sender, null, null, null, replacements, async);
    }

    public static String parsePlaceholder(String placeholder, Player player, boolean async, Object... replacements) {
        return parsePlaceholder(placeholder, player, null, null, null, replacements, async);
    }

    public static String parsePlaceholder(String placeholder, Player player, boolean async) {
        return parsePlaceholder(placeholder, player, null, null, null, null, async);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender, boolean async) {
        return parsePlaceholder(placeholder, sender, null, null, null, null, async);
    }

    public static String parsePlaceholder(String placeholder, Player player) {
        return parsePlaceholder(placeholder, player, null, null, null, null, false);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender) {
        return parsePlaceholder(placeholder, sender, null,  null, null, null, false);
    }

    public static String parsePlaceholder(String placeholder, Player player, Command command, String[] args) {
        return parsePlaceholder(placeholder, player, command, null, args, null, false);
    }

    public static String parsePlaceholder(String placeholder, Player player, Command command, String[] args, boolean async) {
        return parsePlaceholder(placeholder, player, command, null, args, null, async);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender, Command command, String[] args) {
        return parsePlaceholder(placeholder, sender, command, null,  args, null, false);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender, Command command, String[] args, boolean async) {
        return parsePlaceholder(placeholder, sender, command, null, args, null, async);
    }

    public static String parsePlaceholder(String placeholder, Player player, SubCommand subcommand, String[] args) {
        return parsePlaceholder(placeholder, player, null, subcommand, args, null, false);
    }

    public static String parsePlaceholder(String placeholder, Player player, SubCommand subcommand, String[] args, boolean async) {
        return parsePlaceholder(placeholder, player, null, subcommand, args, null, async);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender, SubCommand subCommand, String[] args) {
        return parsePlaceholder(placeholder, sender, null, subCommand,  args, null, false);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender, SubCommand subcommand, String[] args, boolean async) {
        return parsePlaceholder(placeholder, sender, null, subcommand, args, null, async);
    }

    public static String parsePlaceholder(String placeholder, Player player, Command command, SubCommand subcommand, String[] args, Object[] replacements, boolean async) {
        Matcher placeholderValue = Pattern.compile("\\{(.*?)}(?!\\s*})\\s*", Pattern.DOTALL).matcher(placeholder);
        String response;

        while (placeholderValue.find()) {
            PlaceholderRequestEvent event = new PlaceholderRequestEvent(placeholderValue.group(1), player, command, subcommand, args, replacements, async);
            Bukkit.getPluginManager().callEvent(event);
            response = event.getResponse() == null ? "null" : event.getResponse();
            placeholder = placeholder.replace("{" + placeholderValue.group(1) + "}", response);
        }

        return ChatColor.translateAlternateColorCodes('&', placeholder);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender, Command command, SubCommand subcommand, String[] args, Object[] replacements, boolean async) {
        Matcher placeholderValue = Pattern.compile("\\{(.*?)}(?!\\s*})\\s*", Pattern.DOTALL).matcher(placeholder);
        String response;

        while (placeholderValue.find()) {
            PlaceholderRequestEvent event = new PlaceholderRequestEvent(placeholderValue.group(1), sender, command, subcommand, args, replacements, async);
            Bukkit.getPluginManager().callEvent(event);
            response = event.getResponse();
            placeholder = placeholder.replace("{" + placeholderValue.group(1) + "}", response);
        }

        return ChatColor.translateAlternateColorCodes('&', placeholder);
    }

}
