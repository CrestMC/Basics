package me.blurmit.basics.util;

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

    public static String parse(String placeholder) {
        return parse(placeholder, null, null, null, null, false, "");
    }

    public static String parse(String placeholder, String... replacements) {
        return parse(placeholder, null, null, null, null, false, replacements);
    }

    public static String parse(String placeholder, boolean async, String... replacements) {
        return parse(placeholder, null, null, null, null, async, replacements);
    }

    public static String parse(String placeholder, boolean async) {
        return parse(placeholder, null, null, null, null, async, "");
    }

    public static String parse(String placeholder, CommandSender sender, boolean async, String... replacements) {
        return parse(placeholder, sender, null, null, null, async, replacements);
    }

    public static String parse(String placeholder, Player player, boolean async, String... replacements) {
        return parse(placeholder, player, null, null, null, async, replacements);
    }

    public static String parse(String placeholder, Player player, boolean async) {
        return parse(placeholder, player, null, null, null, async, "");
    }

    public static String parse(String placeholder, CommandSender sender, boolean async) {
        return parse(placeholder, sender, null, null, null, async, "");
    }

    public static String parse(String placeholder, Player player) {
        return parse(placeholder, player, null, null, null, false, "");
    }

    public static String parse(String placeholder, CommandSender sender) {
        return parse(placeholder, sender, null,  null, null, false, "");
    }

    public static String parse(String placeholder, Player player, Command command, String[] args) {
        return parse(placeholder, player, command, null, args, false, "");
    }

    public static String parse(String placeholder, Player player, Command command, String[] args, boolean async) {
        return parse(placeholder, player, command, null, args, async, "");
    }

    public static String parse(String placeholder, CommandSender sender, Command command, String[] args) {
        return parse(placeholder, sender, command, null,  args, false, "");
    }

    public static String parse(String placeholder, CommandSender sender, Command command, String[] args, boolean async) {
        return parse(placeholder, sender, command, null, args, async, "");
    }

    public static String parse(String placeholder, Player player, SubCommand subcommand, String[] args) {
        return parse(placeholder, player, null, subcommand, args, false, "");
    }

    public static String parse(String placeholder, Player player, SubCommand subcommand, String[] args, boolean async) {
        return parse(placeholder, player, null, subcommand, args, async, "");
    }

    public static String parse(String placeholder, CommandSender sender, SubCommand subCommand, String[] args) {
        return parse(placeholder, sender, null, subCommand,  args, false, "");
    }

    public static String parse(String placeholder, CommandSender sender, SubCommand subcommand, String[] args, boolean async) {
        return parse(placeholder, sender, null, subcommand, args, async, "");
    }

    public static String parse(String placeholder, Player player, Command command, SubCommand subcommand, String[] args, boolean async, String... replacements) {
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

    public static String parse(String placeholder, CommandSender sender, Command command, SubCommand subcommand, String[] args, boolean async, String... replacements) {
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