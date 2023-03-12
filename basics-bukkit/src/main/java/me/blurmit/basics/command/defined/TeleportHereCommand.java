package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;

public class TeleportHereCommand extends CommandBase {

    private final Basics plugin;

    public TeleportHereCommand(Basics plugin) {
        super(plugin.getName());
        setName("teleporthere");
        setDescription("Teleports the specified player to you");
        setUsage("/teleporthere <player>");
        setAliases(Collections.singletonList("tphere"));
        setPermission("basics.command.teleporthere");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.ONLY_PLAYERS + ""));
                return true;
            }

            Player player = (Player) sender;
            Player target = plugin.getServer().getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_NOT_FOUND + "", args[0]));
                return true;
            }

            target.teleport(player);
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_TELEPORTED_OTHER + "", target.getName(), player.getName()));
            return true;
        }

        sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
        return true;
    }

}
