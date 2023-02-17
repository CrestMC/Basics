package me.blurmit.basicsbungee.util;

import me.blurmit.basicsbungee.event.PlaceholderRequestEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholders {

    public static String parsePlaceholder(String placeholder) {
        return parsePlaceholder(placeholder, null, null, null, false, null);
    }

    public static String parsePlaceholder(String placeholder, String... replacements) {
        return parsePlaceholder(placeholder, null, null, null, false, replacements);
    }

    public static String parsePlaceholder(String placeholder, boolean async) {
        return parsePlaceholder(placeholder, null, null, null, async, null);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender, boolean async, String... replacements) {
        return parsePlaceholder(placeholder, sender, null, null, async, replacements);
    }

    public static String parsePlaceholder(String placeholder, ProxiedPlayer player, boolean async, String... replacements) {
        return parsePlaceholder(placeholder, player, null, null, async, replacements);
    }

    public static String parsePlaceholder(String placeholder, ProxiedPlayer player, boolean async) {
        return parsePlaceholder(placeholder, player, null, null, async, null);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender, boolean async) {
        return parsePlaceholder(placeholder, sender, null, null, async, null);
    }

    public static String parsePlaceholder(String placeholder, ProxiedPlayer player) {
        return parsePlaceholder(placeholder, player, null, null, false, null);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender) {
        return parsePlaceholder(placeholder, sender, null, null, false, null);
    }

    public static String parsePlaceholder(String placeholder, ProxiedPlayer player, Command command, String[] args) {
        return parsePlaceholder(placeholder, player, command, args, false, null);
    }

    public static String parsePlaceholder(String placeholder, ProxiedPlayer player, Command command, String[] args, boolean async) {
        return parsePlaceholder(placeholder, player, command, args, async, null);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender, Command command, String[] args) {
        return parsePlaceholder(placeholder, sender, command, args, false, null);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender, Command command, String[] args, boolean async) {
        return parsePlaceholder(placeholder, sender, command, args, async, null);
    }

    public static String parsePlaceholder(String placeholder, ProxiedPlayer player, Command command, String[] args, boolean async, String... replacements) {
        Matcher placeholderValue = Pattern.compile("\\{(.*?)}(?!\\s*})\\s*", Pattern.DOTALL).matcher(placeholder);
        String response;

        while (placeholderValue.find()) {
            PlaceholderRequestEvent event = new PlaceholderRequestEvent(placeholderValue.group(1), player, command, args, async, replacements);
            ProxyServer.getInstance().getPluginManager().callEvent(event);
            response = event.getResponse() == null ? "null" : event.getResponse();
            placeholder = placeholder.replace("{" + placeholderValue.group(1) + "}", response);
        }

        return ChatColor.translateAlternateColorCodes('&', placeholder);
    }

    public static String parsePlaceholder(String placeholder, CommandSender sender, Command command, String[] args, boolean async, String... replacements) {
        Matcher placeholderValue = Pattern.compile("\\{(.*?)}(?!\\s*})\\s*", Pattern.DOTALL).matcher(placeholder);
        String response;

        while (placeholderValue.find()) {
            PlaceholderRequestEvent event = new PlaceholderRequestEvent(placeholderValue.group(1), sender, command, args, async, replacements);
            ProxyServer.getInstance().getPluginManager().callEvent(event);
            response = event.getResponse();
            placeholder = placeholder.replace("{" + placeholderValue.group(1) + "}", response);
        }

        return ChatColor.translateAlternateColorCodes('&', placeholder);
    }

}
