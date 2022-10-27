package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.placeholder.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;

public class TeleportCommand extends CommandBase {

    private final Basics plugin;

    public TeleportCommand(Basics plugin) {
        super(plugin.getName());
        setName("teleport");
        setDescription("Teleports a player to a specified location or player");
        setUsage("/teleport <location> [new location]");
        setAliases(Collections.singletonList("tp"));
        setPermission("basics.commands.teleport");

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

            player.teleport(target);
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_TELEPORTED + "", target.getName()));
            return true;
        }

        if (args.length == 2) {
            Player target1 = plugin.getServer().getPlayer(args[0]);
            Player target2 = plugin.getServer().getPlayer(args[1]);

            if (target1 == null || target2 == null) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_NOT_FOUND + "", target1 == null ? args[0] : args[1]));
                return true;
            }

            target1.teleport(target2);
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_TELEPORTED_OTHER + "", target1.getName(), target2.getName()));
            return true;
        }

        if (args.length == 3) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.ONLY_PLAYERS + ""));
                return true;
            }

            String coordinates = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
            Player player = (Player) sender;
            Location location;

            try {
                location = new Location(player.getWorld(), Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
            } catch (NumberFormatException e) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_COORDINATES + "", coordinates));
                return true;
            }

            player.teleport(location);
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_TELEPORTED_OTHER + "", player.getName(), coordinates));
            return true;
        }

        if (args.length == 4) {
            String coordinates = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            Player target = plugin.getServer().getPlayer(args[0]);
            Location location;

            if (target == null) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_NOT_FOUND + "", args[0]));
                return true;
            }

            try {
                location = new Location(target.getWorld(), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
            } catch (NumberFormatException e) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_COORDINATES + "", coordinates));
                return true;
            }

            target.teleport(location);
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_TELEPORTED_OTHER + "", target.getName(), coordinates));
            return true;
        }

        sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
        return true;
    }

}
