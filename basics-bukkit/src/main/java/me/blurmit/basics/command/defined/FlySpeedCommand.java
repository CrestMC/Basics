package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FlySpeedCommand extends CommandBase {

    private final Basics plugin;

    public FlySpeedCommand(Basics plugin) {
        super(plugin.getName());
        setName("flyspeed");
        setDescription("Changes the fly speed of a player");
        setUsage("/flyspeed <number> [player]");
        setPermission("basics.command.flyspeed");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parse(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (args.length == 2) {
            if (!sender.hasPermission("basics.command.flyspeed.other")) {
                sender.sendMessage(Placeholders.parse(Messages.NO_PERMISSION_SUBCOMMAND + "", sender, this, args));
                return true;
            }

            Player target = plugin.getServer().getPlayer(args[1]);
            float speed;

            if (target == null) {
                sender.sendMessage(Placeholders.parse(Messages.PLAYER_NOT_FOUND + "", args[1]));
                return true;
            }

            try {
                speed = Float.parseFloat(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Placeholders.parse(Messages.NUMBER_INVALID + "", args[0]));
                return true;
            }

            if (speed > 10F || speed < -10F) {
                sender.sendMessage(Placeholders.parse(Messages.FLY_SPEED_INVALID + "", target, this, args));
                return true;
            }

            target.setFlySpeed(speed / 10);
            sender.sendMessage(Placeholders.parse(Messages.FLY_SPEED_CHANGED + "", target, this, args));
            return true;

        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parse(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        if (args.length == 1) {
            float speed;
            Player player = (Player) sender;

            try {
                speed = Float.parseFloat(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Placeholders.parse(Messages.NUMBER_INVALID + "", args[0]));
                return true;
            }

            if (speed > 10F || speed < -10F) {
                sender.sendMessage(Placeholders.parse(Messages.FLY_SPEED_INVALID + "", player, this, args));
                return true;
            }

            player.setFlySpeed(speed / 10);
            player.sendMessage(Placeholders.parse(Messages.FLY_SPEED_CHANGED + "", player, this, args));
            return true;
        }

        sender.sendMessage(Placeholders.parse(Messages.INVALID_ARGS + "", sender, this, args));
        return true;

    }

}
