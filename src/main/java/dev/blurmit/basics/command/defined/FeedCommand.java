package dev.blurmit.basics.command.defined;

import dev.blurmit.basics.Basics;
import dev.blurmit.basics.command.CommandBase;
import dev.blurmit.basics.util.Placeholders;
import dev.blurmit.basics.util.lang.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class FeedCommand extends CommandBase {

    private final Basics plugin;

    public FeedCommand(Basics plugin) {
        super(plugin.getName());
        setName("feed");
        setDescription("Feed yourself or another player");
        setAliases(Collections.singletonList("feedme"));
        setUsage("Usage: /feed [player]");
        setPermission("basics.commands.feed");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (args.length >= 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_NOT_FOUND + "", args[0]));
                return true;
            }

            target.setSaturation(20);
            target.setFoodLevel(20);

            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_FED + "", target, this, args));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        Player player = (Player) sender;

        player.setSaturation(20);
        player.setFoodLevel(20);

        sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_FED + "", player, this, args));
        return true;
    }
}
