package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.placeholder.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class SocialsCommand extends CommandBase {

    private final Basics plugin;

    public SocialsCommand(Basics plugin) {
        super(plugin.getName());
        setName("socials");
        setDescription("Sends the social media information about the server");
        setUsage("Usage: /socials");
        setPermission("basics.commands.socials");
        setAliases(Arrays.asList("website", "discord", "store", "twitter"));

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String command, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        List<String> allSocials = plugin.getConfigManager().getConfig().getStringList("Social-Media.All");
        String website = plugin.getConfigManager().getConfig().getString("Social-Media.Website");
        String discord = plugin.getConfigManager().getConfig().getString("Social-Media.Discord");
        String store = plugin.getConfigManager().getConfig().getString("Social-Media.Store");
        String twitter = plugin.getConfigManager().getConfig().getString("Social-Media.Twitter");

        switch (command.toLowerCase()) {
            case "socials": {
                allSocials.forEach(social -> sender.sendMessage(Placeholders.parsePlaceholder(social)));
                break;
            }
            case "website": {
                sender.sendMessage(Placeholders.parsePlaceholder(website));
                break;
            }
            case "discord": {
                sender.sendMessage(Placeholders.parsePlaceholder(discord));
                break;
            }
            case "store": {
                sender.sendMessage(Placeholders.parsePlaceholder(store));
                break;
            }
            case "twitter": {
                sender.sendMessage(Placeholders.parsePlaceholder(twitter));
                break;
            }
        }

        return true;
    }

}
